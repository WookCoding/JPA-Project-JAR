package com.app.projectjar.repository.notice;

import com.app.projectjar.entity.member.Member;
import com.app.projectjar.entity.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQueryDSL {
}
