package com.app.projectjar.service.member;

import com.app.projectjar.domain.member.MailDTO;
import com.app.projectjar.domain.member.MemberDTO;
import com.app.projectjar.entity.member.Member;
import com.app.projectjar.provider.UserDetail;
import com.app.projectjar.repository.member.MemberRepository;
import com.app.projectjar.type.BadgeType;
import com.app.projectjar.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Qualifier("member") @Primary
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        화면에서 입력한 회원 아이디를 통해 조회된 정보 (로그인 검사에 사용된다)
        Member member = memberRepository.findByMemberEmail_QueryDSL(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        return UserDetail.builder()
                .id(member.getId())
                .memberEmail(member.getMemberEmail())
                .memberPassword(member.getMemberPassword())
                .memberPhoneNumber(member.getMemberPhoneNumber())
                .memberName(member.getMemberName())
                .memberNickName(member.getMemberNickname())
                .badgeType(member.getBadgeType())
                .memberType(member.getMemberType())
                .build();
    }

//    회원가입
    @Override
    public void join(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        memberDTO.setMemberPassword(passwordEncoder.encode(memberDTO.getMemberPassword()));
        memberDTO.setMemberType(Role.MEMBER);
        memberRepository.save(toMemberEntity(memberDTO));
    }

//    회원정보 조회
    @Override
    public MemberDTO getMemberInfo(Long id) {
        Optional<Member> foundMember = memberRepository.findById(id);
        if (foundMember.isPresent()) {
            return toMemberDTO(foundMember.get());
        }
        return null;
    }

    //    이메일 중복검사
    @Override
    public Long checkEmail(String memberEmail) { return memberRepository.overlapByMemberEmail_QueryDSL(memberEmail);}

//    핸드폰 중복검사
    @Override
    public Long checkPhoneNumber(String memberPhoneNumber) { return memberRepository.overlapByPhoneNumber_QueryDSL(memberPhoneNumber);}

//    닉네임 중복검사
    @Override
    public Long checkNickName(String memberNickName) { return memberRepository.overlapByNickName_QueryDSL(memberNickName);}

//    이메일 / 비밀번호 찾기
    @Override
    public Member getMemberEmail(String memberEmail) {
        return memberRepository.findByMemberEmailNoOptional_QueryDSL(memberEmail);
    }

//    비밀빈호 변경
    @Override
    public void updatePassword(Long id, String memberPassword, PasswordEncoder passwordEncoder) {
        Member member = memberRepository.findMemberById(id);
        member.updatePassword(passwordEncoder.encode(memberPassword));
    }

    //    회원정보 수정
    @Override
    public void updateMember(MemberDTO memberDTO, Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);

        optionalMember.ifPresent(
                member -> {
                    member.setMemberNickname(memberDTO.getMemberNickname());
                    member.setMemberEmail(memberDTO.getMemberEmail());
                    member.setMemberPhoneNumber(memberDTO.getMemberPhoneNumber());
                    member.setMemberStatus(memberDTO.getMemberStatus());
                    memberRepository.save(member);
                }
        );
    }

    @Override
    public MemberDTO getMember(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return toMemberDTO(member.get());
    }

    @Override
    public Optional<Member> getOptionalMember(Long id) {
        return memberRepository.findByMemberId_QueryDSL(id);
    }

    //    뱃지 횟수 조회
    @Override
    public int getMemberBadgeCount(Long id) {
        return memberRepository.findByIdWithAttendCount_QueryDsl(id);
    }

//    뱃지 업데이트
    @Override
    public void updateBadge(Long id) {
        memberRepository.findById(id).ifPresent(member -> {
            int totalCount = memberRepository.findByIdWithAttendCount_QueryDsl(member.getId());

            if (totalCount >= 10) {
                member.setBadgeType(BadgeType.ONE);
            } else if (totalCount >= 20) {
                member.setBadgeType(BadgeType.TWO);
            } else if (totalCount >= 30) {
                member.setBadgeType(BadgeType.THREE);
            } else {
                member.setBadgeType(BadgeType.ZERO);
            }
            memberRepository.save(member);

        });
    }

    //    인증 문자 날리기 // 실제로 문자가 날라가니까 막아놓기
    @Override
    public void checkSMS(String memberPhone, String code) {
        String api_key = "NCSSJHRC2MJLJCUB";                        //"API Key 들어갈 곳";
        String api_secret = "3GBNEKA1VJXQCWTW64EBXU0QF26U6VBK";     //"API Secret Key 들어갈 곳";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", memberPhone);    // 수신전화번호
        params.put("from", "01089151820");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "인증번호는" + "[" + code + "]" + "입니다.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

    @Override
    public Member findMemberByRandomKey(String randomKey) {
        return memberRepository.findMemberByRandomKey(randomKey);
    }

    @Override
    public Member findMemberByMemberEmailAndRandomKey(String memberEmail, String randomKey) {
        return findMemberByMemberEmailAndRandomKey(memberEmail, randomKey);
    }

    @Override
    public void sendMail(MailDTO mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setFrom("officail.jarjar@gmail.com");
//        from 값을 설정하지 않으면 application.yml의 username값이 설정됩니다.
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);
    }

    @Override
    public Page<MemberDTO> getAllMembersWithPaging(int page) {
        Page<Member> members = memberRepository.findAllByMemberId_QueryDsl(PageRequest.of(page, 10));
        List<MemberDTO> memberDTOS = members.getContent().stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(memberDTOS, members.getPageable(), members.getTotalElements());
    }

    @Override
    public void deleteMembers(List<Long> memberIds) {
        for (Long memberId : memberIds) {
            memberRepository.deleteById(memberId);
        }
    }

    @Override
    public Long findPersonalAttendCountByMemberId(Long id) {
        return memberRepository.findPersonalAttendCountByMemberId(id);
    }

    @Override
    public Long findGroupAttendCountByMemberId(Long id) {
        return memberRepository.findGroupAttendCountByMemberId(id);
    }

}
