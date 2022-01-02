# springboot jooq
#### jooq 를 적용을 위해 정리 중입니다. 잘못된 점이 있을 수도 있습니다.
#### 정리 이유
* jpa 와 querydsl을 경험해보니.. ~~mybatis 가 너무 쓰기 싫었다..~~ 
* mybatis 로만 되어있는 프로젝트는 jpa, querydsl 로 변경하기엔 시간이 많이 필요하다고 생각.
* jooq 를 찾아보게 되었고 조금씩 적용중 입니다.
* querydsl 도 querydsl(sql) 을 지원 합니다.

#### sql (string, xml) 단점
* Type check 가 불가능, 실행하기 전까지 모름
* 실행 전 오류를 알기 어렵다.

## DSL (Domain Specific Language)
#### DSL 장점
* 동적 쿼리 지원
* java 코드로 작성하기 때문에 컴파일 시에 문법 오류 발견 가능
* Type Safe 지원
* 오타로 인한 오류를 줄일 수 있음
* 자동완성 지원
* 문법적으로 잘못된 쿼리를 일부 방지 할 수 있음
  * [참조 : jooq BNF 표기법](https://zepinos.tistory.com/58?category=810346)
  * [참조 : querydsl fluent api](http://querydsl.com/static/querydsl/4.1.3/reference/html_single/#preface)

#### DSL 단점
* codegen 파일생성(Q파일) 이라고 생각.(꼬이면 한없이 안됨..)
* 익숙하지 않다.

#### jooq vs querydsl(jpa)
|jooq|querydsl(jpa)|
|----|--------|
|무료 DBMS(opensource)에서 무료|모든 DBMS 무료|
|연관관계 없이 조인 가능|hibernate5.1부터 연관관계 없이 조인 지원|
|subquery 다 가능한듯|where, select subquery 가능, from절 지원 안함|
|query와 비슷|jpa,객체지향 이해 필요(영속성 컨텍스트, 연관관계, n + 1 문제 등.. 제대로 쓰기 어렵다)|   
* reference
  * [jooq Scalar subqueries](https://www.jooq.org/doc/3.11/manual/sql-building/column-expressions/scalar-subqueries/) 
  * [jooq Derived tables](https://www.jooq.org/doc/3.11/manual/sql-building/table-expressions/derived-tables/)
  * [about jooq](https://zepinos.tistory.com/54?category=810346)
  * [querydsl subquery](https://jojoldu.tistory.com/379)
  * [querydsl join](https://dev-alxndr.tistory.com/32)
  * [sub query select join](https://velog.io/@gwontaeyong/JOOQ-%EC%84%A4%EC%A0%95) 
  * [querydsl from, join절에 서브쿼리 사용하기](https://programmingnote.tistory.com/85)

## environment
* intellij
* springboot 2.6.2
* jdk 11
* jooq gradle plugin 6.0.1
* jooq 3.14.15
* jooq-meta-extentions 3.14.15

## jooq my example
- 직접 짜본 필요한 예제들..(코드가 개판이라 ㅜㅜ)
- jooq repository
  - insert(returning), select, batch, insertOnduplicate 
    - [AuthorRepository](https://github.com/ingduk2/springboot-jooq-practice/blob/master/src/main/java/com/hello/jooq/example/author/repository/JooqAuthorRepository.java)
  - insert(returning, store), dynamicquery(join) 
    - [ArticleRepository](https://github.com/ingduk2/springboot-jooq-practice/blob/master/src/main/java/com/hello/jooq/example/author/repository/JooqArticleRepository.java)
- jooq unitTest
  - [AuthorTest](https://github.com/ingduk2/springboot-jooq-practice/blob/master/src/test/java/com/hello/jooq/example/author/repository/JooqArticleRepositoryTest.java)
  - [ArticleTest](https://github.com/ingduk2/springboot-jooq-practice/blob/master/src/test/java/com/hello/jooq/example/author/repository/JooqArticleRepositoryTest.java)  

## jooq official example
- 문서가 잘되어있어서 검색하면 예제 나옴
- insert, insertreturning
  - [jooq official insert](https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/insert-statement/)
  - [jooq official insertreturning](https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/insert-statement/insert-returning/)
- select, join, dynamicquery
  - [jooq official select](https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/)
  - [jooq official join](https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/join-clause/)
  - [jooq official dynamicquery](https://www.jooq.org/doc/latest/manual/sql-building/dynamic-sql/)
  - [jooq blog dynamicquery with function](https://blog.jooq.org/a-functional-programming-approach-to-dynamic-sql-with-jooq/)
- batch insert, batch insertOnDuplicate
  - [jooq official batch](https://www.jooq.org/doc/3.15/manual/sql-execution/batch-execution/)
  - [jooq official insertOnDuplicate](https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/insert-statement/insert-on-duplicate-key/)
- Loading POJOs back into Records to store them(store)
  - [jooq official store](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/pojos/#N8D4F1)
  
## setting
##### jooq codegen
- gradle third party 사용해야 잘됨.
- 1.db 연결해서 가져오는 방식 (단점은 db 못붙는 경우는 애매함)
  - [build.gradle](https://github.com/ingduk2/springboot-jooq-practice/blob/master/build.gradle_jooq_codegen_accessDB)
- 2.DDL sql create 파일로 하는 방식 (sql 파일 넣어줘야하는데 한번 넣으면 되니 괜찮은듯)
  - [build.gradle](https://github.com/ingduk2/springboot-jooq-practice/blob/master/build.gradle)   
- 3.xml Schema (복잡..) 
- 4.From Jpa (JPA와 같이 사용하는 경우에 좋을듯)
- reference [jooq codegen 종류](https://kwonnam.pe.kr/wiki/gradle/jooq_codegen)

##### jooq build codegen
- ./gradlew generateJooq
- ./gradlew cleanGenerateJooq
- gradle build 시 자동으로 생성.

##### jooq test
- h2 memory db yml 추가
- embedded schema 추가
- test Code를 build시 접속 못 할 수도 있음(jenkins에서 db접속 안될때..)
  - [test/resource](https://github.com/ingduk2/springboot-jooq-practice/tree/master/src/test/resources)
- junit jooqTest (repository Import 필요 Autowired 사용할려면)

##### 주의사항
- test h2 schema 사용시 schema.sql CREATE IF NOT EXIST 사용.
- 없으니 테스트 전체 돌릴때 table exist Exception이 남..
- 다른 설정으로도 가능할듯..?

##### 설정
1. application.yml jooq sqldialect 추가
2. build.gradle 추가
3. resources/sql파일 추가
4. gradle task - jooq - generateJooq
5. build 폴더에 스키마 생성되었는지 확인

##### reference
* [ddl codegen](https://stackoverflow.com/questions/70262959/function-not-found-when-generating-code-with-ddl-database-jooq)
* [gradle jooq plugin](https://github.com/etiennestuder/gradle-jooq-plugin)
* [gradle db codegen](https://oops4u.tistory.com/2577)
* [springboot-jooq codegen](https://ellune.tistory.com/56)
* [jooq](https://www.jooq.org/doc/3.14/manual/)
* [jooq-spring](https://zepinos.tistory.com/55?category=810346)
* [jooq-spring](https://sightstudio.tistory.com/54)
* [jooq-with-spring](https://www.baeldung.com/jooq-with-spring)
