package kr.co.pick.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.pick.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @PostMapping("/input")
    public ResponseEntity<Long> handleTagInfo(@RequestBody Map<String, String> tagInfo, HttpServletRequest request) {
        String skinColor = tagInfo.get("skinColor");
        String skinType = tagInfo.get("skinType");
        String pcolor = tagInfo.get("pcolor");

        System.out.println("TagController: " + skinColor + ", " + skinType + ", " + pcolor);

        Long tagId = tagService.getTagId(skinColor, skinType, pcolor);
        return ResponseEntity.ok(tagId);
    }
}
