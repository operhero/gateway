call mvn clean:clean
call mvn -Dmaven.test.skip=true package -U
@pause