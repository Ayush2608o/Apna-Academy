package com.apnaacademy.admin.controller;

import com.apnaacademy.service.AdminTableService;
import com.apnaacademy.service.AcademyDataService;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminCrudController {
    private final AdminTableService tables;
    private final AcademyDataService data;

    public AdminCrudController(AdminTableService tables, AcademyDataService data) {
        this.tables = tables;
        this.data = data;
    }

    @GetMapping("/admin/manage/{module}")
    public String list(@PathVariable String module, Model model) {
        String table = tables.resolve(module);
        model.addAttribute("module", module);
        model.addAttribute("title", tables.title(module));
        model.addAttribute("columns", tables.columns(table));
        model.addAttribute("rows", tables.rows(table));
        if (module.equals("certificates")) {
            model.addAttribute("students", data.certificateStudents());
        }
        return "admin/table";
    }

    @GetMapping("/admin/certificates/issue")
    public String issueCertificateForm(Model model) {
        model.addAttribute("students", data.certificateStudents());
        return "admin/certificate-issue";
    }

    @PostMapping("/admin/manage/certificates/issue")
    public String issueCertificate(@RequestParam long studentId, RedirectAttributes redirect) {
        try {
            String certificateNumber = data.issueCertificate(studentId);
            redirect.addFlashAttribute("success", "Certificate generated: " + certificateNumber);
            return "redirect:/certificate/" + certificateNumber;
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        } catch (DataAccessException ex) {
            redirect.addFlashAttribute("error",
                    "Certificate could not be generated. Check the student record and assigned course.");
        }
        return "redirect:/admin/manage/certificates";
    }

    @PostMapping("/admin/manage/{module}/create")
    public String create(@PathVariable String module, @RequestParam MultiValueMap<String, String> form,
                         RedirectAttributes redirect) {
        try {
            tables.insert(tables.resolve(module), firstValues(form));
            redirect.addFlashAttribute("success", "Record created.");
        } catch (DataAccessException | IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", "Record could not be saved. Check required fields and related IDs.");
        }
        return "redirect:/admin/manage/" + module;
    }

    @PostMapping("/admin/manage/{module}/{id}/update")
    public String update(@PathVariable String module, @PathVariable long id,
                         @RequestParam MultiValueMap<String, String> form, RedirectAttributes redirect) {
        try {
            tables.update(tables.resolve(module), id, firstValues(form));
            redirect.addFlashAttribute("success", "Record updated.");
        } catch (DataAccessException | IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", "Record could not be updated. Check field values and related IDs.");
        }
        return "redirect:/admin/manage/" + module;
    }

    @PostMapping("/admin/manage/{module}/{id}/delete")
    public String delete(@PathVariable String module, @PathVariable long id, RedirectAttributes redirect) {
        try {
            tables.delete(tables.resolve(module), id);
            redirect.addFlashAttribute("success", "Record deleted.");
        } catch (DataAccessException | IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", "Record could not be deleted because it may be referenced by another record.");
        }
        return "redirect:/admin/manage/" + module;
    }

    private Map<String, String> firstValues(MultiValueMap<String, String> form) {
        return form.entrySet().stream().filter(entry -> !entry.getKey().equals("_csrf"))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().isEmpty() ? "" : entry.getValue().getFirst()));
    }
}
