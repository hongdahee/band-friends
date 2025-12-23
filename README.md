# 🎸 밴드프렌즈

밴드 멤버를 구인하고 실시간으로 소통할 수 있는 밴드 매칭 플랫폼입니다.  
사용자의 위치를 기반으로 가까운 구인 글을 확인하고, 밴드 멤버 간 실시간 채팅이 가능합니다.

---

## 📚 목차

1. [소개](#소개)
2. [기술 스택](#기술-스택)
3. [주요 기능](#주요-기능)
4. [상세 화면](#상세-화면)

---

## 소개

같이 악기 연주할 밴드 멤버를 쉽게 찾고, 실시간으로 소통할 수 있는 웹 서비스입니다.  
권한 처리, 실시간 채팅, 위치 기반 검색, S3 이미지 업로드 등 다양한 기술을 적용했습니다.

---

## 기술 스택

- **Backend**: Spring Boot, Spring Security, JPA, WebSocket
- **Database**: MySQL
- **Storage**: AWS S3
- **Test**: JUnit

---

## 주요 기능

- ✅ WebSocket을 이용한 밴드 간 실시간 채팅
- ✅ 사용자의 위치 인증 → 30km 이내 구인 글만 노출
- ✅ 비정상 조회수 방지를 위한 동일 유저 1일 1회 조회수 증가 제한
- ✅ AWS S3 이미지 업로드, 유저/밴드 삭제 시 이미지 자동 제거
- ✅ 밴드 멤버 권한 체크 → 403 Forbidden 에러 처리로 보안 강화

---

## 상세 화면
### 자유게시판
<img width="1440" alt="스크린샷 2025-05-29 오전 10 20 52" src="https://github.com/user-attachments/assets/fb75f4bb-23e2-441a-b4e4-78b6b683d405" />

### 자유게시판 글 작성
<img width="1440" alt="스크린샷 2025-05-29 오전 10 20 05" src="https://github.com/user-attachments/assets/fad026da-cdcc-411e-8b9a-4e3ef84f6091" />

### 자유게시판 글 상세페이지
<img width="1440" alt="스크린샷 2025-05-29 오전 10 27 22" src="https://github.com/user-attachments/assets/a8856877-7535-49ac-a98c-5876b6920dea" />

### 멤버 구인 게시판
<img width="1440" alt="스크린샷 2025-05-29 오전 10 25 37" src="https://github.com/user-attachments/assets/57031479-7ff8-4ea9-a390-6e087d7f844c" />

### 멤버 구인 글 상세페이지
<img width="1440" alt="스크린샷 2025-05-29 오전 10 33 32" src="https://github.com/user-attachments/assets/92a72bc1-2d15-4a73-81fa-91624026ac16" />

### 밴드 가입 신청
<img width="1440" alt="스크린샷 2025-05-29 오전 10 31 43" src="https://github.com/user-attachments/assets/36275ae7-bfd4-49d5-a74a-733b5ccca6d3" />

### 밴드 가입 신청 관리
<img width="1440" alt="스크린샷 2025-05-29 오전 10 32 25" src="https://github.com/user-attachments/assets/e9cd1929-1c3c-4e81-890b-65b89db0a296" />

### 밴드 홈
<img width="1440" alt="스크린샷 2025-05-29 오전 10 34 43" src="https://github.com/user-attachments/assets/64ab5779-d388-4937-8ed2-66f65a10e681" />

### 밴드 관리
<img width="1440" alt="스크린샷 2025-05-29 오전 10 35 22" src="https://github.com/user-attachments/assets/7fa42ea8-2802-4d8b-8ff7-61924b086b7c" />

### 밴드 멤버 관리
<img width="1440" alt="스크린샷 2025-05-29 오전 10 35 48" src="https://github.com/user-attachments/assets/46be895c-501a-4207-9b93-52b277ff6df6" />

### 밴드 그룹채팅, 합주실 지도
<img width="1440" alt="스크린샷 2025-05-29 오전 10 38 06" src="https://github.com/user-attachments/assets/a5f57523-15bf-4e91-8f91-2211b6ba5f6c" />

### 위치 인증
<img width="1440" alt="스크린샷 2025-05-29 오전 10 39 41" src="https://github.com/user-attachments/assets/c3666bf6-eef5-4c91-b06b-f28c346e1f6e" />
