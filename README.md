# 📉 SearchMovie

## 🧾 프로젝트 소개

- 영화 쿠폰 발급 및 영화 조회 시스템
- 영화와 영화 쿠폰 관리  REST API 기반의 안정적인 백엔드 서버 구축
- 프로젝트 기간: 2025.12.31 ~ 2025.01.09

## 🧾 프로젝트 목표
- 쿠폰 발급 과정에서 발생할 수 있는 동시성 문제를 해결
- 대규모 트래픽 대응
  - Redis 활용
  - 영화 더미데이터 10만건 이상 테스트
- 캐싱을 활용한 응답 속도 향상

## 🌏 개발 환경

- **OS**: Mac OS
- **IDE**: IntelliJ IDEA
- **Language**: Java 17
- **Build Tool**: Gradle
- **Version Control**: Git, GitHub
- **Test Tool**: Postman, NGrinder, JUnit

## 🛠️ 기술 스택

- **Language**: Java
- **Framework**: Spring Boot 3
- **ORM**: Spring Data JPA, Hibernate
- **Database**: MySQL, redis
- **Security:** Spring Security
- **Validation:** Bean Validation

## 기술 스택 사용 이유
- **Redis**
    - 사용하고 있는 프레임워크인 스프링과의 호환성이 좋고 캐시 읽기가 가능하여 db의 성능유지를 서포팅할 수 있어 채용함
    - redis에서는 값을 문자열로밖에 못 받아오기에 값을 문자열로 저장하는 자료구조인 Redis template을 활용
- **MySQL**
    - 여러 엔티티와 연관관계를 맺고 있는 시스템 특성상 관계형 데이터베이스를 채용
    - 
- 1) Remote Cache 를 위한 다양한 라이브러리(Memcached 등) 들이 있을텐데 그 중에서 Redis 를 선택한 이유가 무엇일까?
  2) Redis Cache 에 데이터를 저장할 때 사용한 자료구조는 무엇이고, 다양한 자료구조중에 해당 자료구조를 선택한 이유는 무엇일까?
  3) RDBMS(SQL) 와 NoSQL 의 차이점은 무엇일까?
  

## 주요 구현 기능
- 10만건 이상의 데이터 관리 
- 대용량 트래픽 대응 기능 구현
  - index, redis, query dsl 적용으로 트래픽 처리량 40배 증가 효과
  - Optimistic Lock 적용으로 동시성 문제 해결
 

## 💫 와이어프레임과 ERD 설계 및 API 명세서

- 와이어프레임
  <img width="1021" height="787" alt="image" src="https://github.com/user-attachments/assets/39885338-8abe-428c-bb0c-619450ff86ef" />


- ERD 설계
<img width="2048" height="797" alt="image" src="https://github.com/user-attachments/assets/16f3ac50-a1a5-44fd-b0f2-ab949acf05af" />


- 주요 테이블
    - `coupon_inventories`: 쿠폰 재고
    - `coupons`: 쿠폰
    - `genres`: 장르
    - `issued_coupon_histories`: 발급 쿠폰 기록
    - `movie_genres`: 영화 장르(영화와 장르 연결)
    - `movies`: 영화
    - `reviews`: 리뷰
    - `search_logs`: 검색 기록
    - `users`: 회원


- API 상세설명 : https://www.notion.so/teamsparta/2cb2dc3ef51481b28cb6feb491c41c44?v=2cb2dc3ef514815cbd0a000c37ebb10a&source=copy_link




## 🧩 프로젝트 구조
```
C:.
├─java
│  └─com
│      └─searchmovie
│          │  SearchMovieApplication.java
│          │
│          ├─common
│          │  ├─config
│          │  │      QuerydslConfig.java
│          │  │      RedisConfig.java
│          │  │      SecurityConfig.java
│          │  │
│          │  ├─entity
│          │  │      BaseEntity.java
│          │  │
│          │  ├─enums
│          │  │      ExceptionCode.java
│          │  │
│          │  ├─exception
│          │  │      CustomException.java
│          │  │      GlobalExceptionHandler.java
│          │  │      SearchException.java
│          │  │
│          │  ├─filter
│          │  │      JwtFilter.java
│          │  │
│          │  ├─model
│          │  │      CommonResponse.java
│          │  │      PageResponse.java
│          │  │
│          │  └─utils
│          │          JwtUtil.java
│          │
│          └─domain
│              ├─auth
│              │  ├─controller
│              │  │      AuthController.java
│              │  │
│              │  ├─dto
│              │  │      LoginRequest.java
│              │  │      LoginResponse.java
│              │  │
│              │  ├─model
│              │  │      LoginRequest.java
│              │  │      LoginResponse.java
│              │  │
│              │  └─service
│              │          AuthService.java
│              │
│              ├─coupon
│              │  ├─controller
│              │  │      CouponController.java
│              │  │
│              │  ├─entity
│              │  │      Coupon.java
│              │  │      IssuedCouponHistory.java
│              │  │      IssuedCouponStatus.java
│              │  │
│              │  ├─model
│              │  │  ├─dto
│              │  │  │      CouponInfo.java
│              │  │  │
│              │  │  ├─request
│              │  │  │      CouponCreateRequest.java
│              │  │  │      CouponUpdateRequest.java
│              │  │  │
│              │  │  └─response
│              │  │          CouponCreateResponse.java
│              │  │          CouponGetResponse.java
│              │  │          IssuedCouponHistoryGetDetailResponse.java
│              │  │          IssuedCouponHistoryIssueResponse.java
│              │  │          IssuedCouponHistoryUseResponse.java
│              │  │
│              │  ├─repository
│              │  │      CouponRepository.java
│              │  │      IssuedCouponHistoryRepository.java
│              │  │
│              │  └─service
│              │          CouponService.java
│              │
│              ├─couponStock
│              │  ├─controller
│              │  │      CouponStockController.java
│              │  │
│              │  ├─entity
│              │  │      CouponStock.java
│              │  │
│              │  ├─model
│              │  │  ├─request
│              │  │  │      CouponStockCreateRequest.java
│              │  │  │      CouponStockUpdateRequest.java
│              │  │  │
│              │  │  └─response
│              │  │          CouponStockCreateResponse.java
│              │  │          CouponStockGetResponse.java
│              │  │          CouponStockUpdateResponse.java
│              │  │
│              │  ├─repository
│              │  │      CouponStockRepository.java
│              │  │
│              │  └─service
│              │          CouponCoreService.java
│              │          CouponStockService.java
│              │
│              ├─movie
│              │  ├─controller
│              │  │      MovieController.java
│              │  │      MovieSearchController.java
│              │  │
│              │  ├─entity
│              │  │      Genre.java
│              │  │      Movie.java
│              │  │      MovieGenre.java
│              │  │
│              │  ├─model
│              │  │  ├─request
│              │  │  │      MovieCreateRequest.java
│              │  │  │      MovieUpdateRequest.java
│              │  │  │
│              │  │  └─response
│              │  │          MovieBaseResponse.java
│              │  │          MovieCreateResponse.java
│              │  │          MovieGenreResponse.java
│              │  │          MovieGetResponse.java
│              │  │          MovieSearchResponse.java
│              │  │          MovieSelectCreateResponse.java
│              │  │          SimplePageResponse.java
│              │  │
│              │  ├─repository
│              │  │      GenreRepository.java
│              │  │      MovieGenreRepository.java
│              │  │      MovieRepository.java
│              │  │      MovieSearchCustomRepository.java
│              │  │      MovieSearchCustomRepositoryImpl.java
│              │  │
│              │  ├─service
│              │  │      MovieSearchCacheService.java
│              │  │      MovieSearchService.java
│              │  │      MovieService.java
│              │  │
│              │  └─support
│              │          GenreNormalizer.java
│              │
│              ├─review
│              │  ├─controller
│              │  │      ReviewController.java
│              │  │
│              │  ├─entity
│              │  │      Review.java
│              │  │
│              │  ├─model
│              │  │  ├─dto
│              │  │  │      ReviewDto.java
│              │  │  │
│              │  │  ├─request
│              │  │  │      ReviewCreateRequest.java
│              │  │  │      ReviewUpdateRequest.java
│              │  │  │
│              │  │  └─response
│              │  │          ReviewCreateResponse.java
│              │  │          ReviewGetResponse.java
│              │  │          ReviewListResponse.java
│              │  │          ReviewResponse.java
│              │  │
│              │  ├─repository
│              │  │      ReviewRepository.java
│              │  │
│              │  └─service
│              │          ReviewService.java
│              │
│              ├─search
│              │  ├─controller
│              │  │      HotKeywordController.java
│              │  │
│              │  ├─entity
│              │  │      HotKeyword.java
│              │  │
│              │  ├─model
│              │  │  ├─request
│              │  │  │      PeriodSearchRequest.java
│              │  │  │
│              │  │  └─response
│              │  │          GenreKeywordResponse.java
│              │  │          HotKeywordResponse.java
│              │  │          PeriodKeywordResponse.java
│              │  │          PeriodSearchResponse.java
│              │  │          SearchGenresResponse.java
│              │  │          SearchKeywordResponse.java
│              │  │          SearchPeriodResponse.java
│              │  │
│              │  ├─repository
│              │  │      HotKeywordRepository.java
│              │  │      HotKeywordRepositoryCustom.java
│              │  │      HotKeywordRepositoryImpl.java
│              │  │
│              │  └─service
│              │          HotKeywordCacheService.java
│              │          HotKeywordService.java
│              │
│              └─user
│                  ├─controller
│                  │      UserController.java
│                  │
│                  ├─dto
│                  │  ├─request
│                  │  │      UserCreateRequest.java
│                  │  │      UserUpdateRequest.java
│                  │  │
│                  │  └─response
│                  │          UserCreateResponse.java
│                  │          UserGetResponse.java
│                  │          UserUpdateResponse.java
│                  │
│                  ├─entity
│                  │      User.java
│                  │      UserRole.java
│                  │
│                  ├─model
│                  │  ├─request
│                  │  │      UserCreateRequest.java
│                  │  │      UserUpdateRequest.java
│                  │  │
│                  │  └─response
│                  │          UserCreateResponse.java
│                  │          UserGetResponse.java
│                  │          UserUpdateResponse.java
│                  │
│                  ├─repository
│                  │      UserRepository.java
│                  │
│                  ├─service
│                  │      UserService.java
│                  │
│                  └─userEnum
└─resources
    │  application.yml
    │
    └─data
            coupons_dummy_50000.csv
            genres.csv
            issued_coupon_histories_50000.csv
            movies.csv
            movie_genres.csv
            users.csv
```

## 🌠 주요 기능

### 1. 인증 인가 (Auth)

- 로그인
- 권한 종류 ADMIN, USER
- 개인 정보 조회와 로그인, 회원가입을 제외한 모든 api에 권한 부여
- 영화와 쿠폰, 쿠폰 재고 관리api는 ADMIN 권한 부여 

### 2. 사용자 (User)

- 회원 가입
- 내 정보 조회, 수정
- 회원 탈퇴

### 3. 영화 (Movie)

- 영화 생성
- 영화 단건 조회
- 영화 전체 조회
- 영화 수정
- 영화 삭제

### 4. 리뷰 (Review)

- 리뷰 생성
- 리뷰 단건 조회
- 리뷰 전체 조회(페이징)
- 리뷰 수정
- 리뷰 삭제

### 5. 검색 (Search)

- 종합 인기검색어 top10 조회
- 장르별 인기검색어 top 10 조회
- 월간 인기검색어 top 10 조회

### 6. 쿠폰 (Coupon)

- 쿠폰 발급
- 내 쿠폰 목록 조회
- 쿠폰 사용
- 로그 기록 대상
- 쿠폰 생성
- 쿠폰 단건 조회
- 쿠폰 전체 조회
- 쿠폰 수정
- 쿠폰 삭제

### 7. 쿠폰 재고 (CouponStock)

- 쿠폰 재고 추가
- 쿠폰 재고 조회
- 쿠폰 재고 수정

### 8. 인증 및 보안(Auth)
- JWT 기반 인증
    - 로그인 시 토큰 발급
- 인증 필요 API
    - 회원가입, 로그인 제외 전부
    - 
- 로그인 
- 비밀번호 검증


## 🧾 트러블 슈팅
- 쿠폰 재고를 검증할 때 쿠폰 서비스와 재고 서비스 양 쪽에서 검증을 시행하여 동시성 이슈가 발생
  - 재고 차감의 책임이 쿠폰 서비스와 재고 서비스에 분산된 것이 문제
  -> 재고 차감의 책임을 쿠폰 서비스에서만 지도록 재고 서비스에서 수행하던 검증 로직을 삭제





## 🧾 협업 방식 및 규칙

| 이름   | 포지션   | 담당(개인별 기여점)                                                                                                         | Github 링크                       |
|--------|----------|-----------------------------------------------------------------------------------------------------------------------------|-----------------------------------|
| 백은서 | 리더     | 영화 전체 검색 API, 영화 키워드 검색 로그 API, 쿠폰 발급 동시성 (낙관적)락 부여 | [🍁 깃헙링크] https://github.com/LemonCoding99  |
| 김진찬 | 팀원     | JWT 기반 인증/인가, 유저 CRUD, 쿠폰 재고 CRUD                                | [🍁 깃헙링크] https://github.com/shortring      |
| 오은지 | 팀원     | 리뷰 CRUD, 쿠폰 CRUD                                                        | [🍁 깃헙링크] https://github.com/oezy-coder     |
| 정순관 | 팀원     | 영화 CRUD, 쿠폰 정책 CRUD, 더미 데이터 추출 및 생성                           | [🍁 깃헙링크] https://github.com/uhk561         |
| 정하륜 | 팀원     | 영화 인기 검색 차트 API, 쿠폰 발급 동시성 (비관적)락 부여                      | [🍁 깃헙링크] https://github.com/jyop1212hy     |
