# 📝 Firebase 기반 메모장 앱 (Memo Keeper)

> Firebase를 기반으로 한 개인용 메모장 앱입니다.  
> 안드로이드(Java)와 Firebase Authentication/Realtime Database/Storage를 활용하여  
> 메모 작성, 이미지 저장, 검색, 비밀번호 설정 기능 등을 제공합니다.

---

## 📌 프로젝트 개요

- **프로젝트 명**: Firebase 메모장 앱
- **개발 기간**: 2023.11 ~ 2023.12
- **개발 환경**: Android Studio (Java), Firebase Realtime DB / Auth / Storage
- **개발자**: 황도균 (컴퓨터공학과, 동의대학교)  
  - 이메일: 20192161@office.deu.ac.kr

---

## 🧩 주요 기능

### 👤 사용자 인증
- Firebase Authentication 기반 이메일 로그인/회원가입
- 로그인 상태 유지 및 자동 로그인

### 🗂️ 메모 관리
- 메모 작성 및 저장 (텍스트 + 이미지)
- 메모 목록 조회
- 메모 검색 (제목/내용 기반)
- 메모 비밀번호 잠금 기능 (선택 적용)
- 메모 상세 확인 및 편집

### 🎨 그림 메모
- DrawingView를 통한 자유 그리기 기능
- 저장 시 이미지 파일로 Firebase Storage에 업로드

### 🔐 사용자 정보 관리
- 로그아웃, 회원탈퇴 기능 제공
- 탈퇴 시 관련 DB 및 계정 정보 삭제 처리

---

## ⚙️ 사용 기술 스택

| 분류        | 기술                                                      |
|-------------|-----------------------------------------------------------|
| **Language**  | Java (Android)                                           |
| **Backend**   | Firebase Realtime Database, Firebase Auth, Firebase Storage |
| **Library**   | Glide (이미지 로딩), Google Firebase SDK                |
| **UI 구성**   | XML 기반 레이아웃 + 커스텀 Adapter                        |
| **IDE**       | Android Studio                                           |
| **버전 관리** | GitHub 사용                                               |

---

## 📱 주요 화면

| 화면명            | 설명                                   |
|-------------------|----------------------------------------|
| 로그인 화면       | 이메일 기반 로그인/회원가입 기능 제공 |
| 메모 목록         | 작성된 메모를 리스트 형태로 표시       |
| 메모 작성         | 텍스트 입력 + 그림 그리기 기능 제공    |
| 메모 상세 확인    | 개별 메모 보기 (비밀번호 확인 포함)    |
| 회원 탈퇴/로그아웃 | 계정 삭제 및 앱 종료 기능              |

---

