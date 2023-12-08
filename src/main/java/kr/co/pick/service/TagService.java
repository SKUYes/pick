package kr.co.pick.service;

import kr.co.pick.dto.request.TagReqDto;
import kr.co.pick.entity.Tag;
import kr.co.pick.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    // 회원 가입할 때 skinColor, skinType, pColor 읽어 와서 PK 값 찾기
    public Long getTagId(String skinColor, String skinType, String pcolor) {
        System.out.println("TagService : " + skinColor + ", " + skinType + ", " + pcolor);

        Optional<Tag> tagOptional = tagRepository.findBySkinColorAndSkinTypeAndPcolor(skinColor, skinType, pcolor);
        System.out.println(tagOptional.get().getId());

        return tagOptional.map(Tag::getId).orElse(null);
    }

}
