##### jooq codegen
- gradle third party 사용해야 잘됨.
- 1.db 연결해서 가져오는 방식 (단점은 db 못붙으면 dsl파일생성 못해서 꺠짐)
- 2.DDL sql create 파일로 하는 방식 (sql 파일 넣어줘야하는데 한번 넣으면 되니 괜찮은듯)
- 3.xml Schema (복잡..) 
- 4.From Jpa (JPA와 같이 사용하는 경우에 좋을듯)
###### reference
- [jooq codegen](https://kwonnam.pe.kr/wiki/gradle/jooq_codegen)

##### jooq build codegen
- ./gradlew generateJooq
- ./gradlew cleanGenerateJooq
- gradle build 시 자동으로 생성.

##### jooq test
- h2 memory db yml 추가
- embedded schema 추가
- test Code를 build시 접속 못 할 수도 있음(jenkins에서 db접속 안될때..)

##### 주의사항
- test h2 schema 사용시 schema.sql CREATE IF NOT EXIST 사용.
- 없으니 테스트 전체 돌릴때 table exist Exception이 남..
- 다른 설정으로도 가능할듯..?(dirtycontext 인가 찾아봐야함..)

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
