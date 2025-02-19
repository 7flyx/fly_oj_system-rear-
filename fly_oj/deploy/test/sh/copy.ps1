rm ../flyoj-jar/gateway/oj-gateway.jar
rm ../flyoj-jar/friend/oj-friend.jar
rm ../flyoj-jar/job/oj-job.jar
rm ../flyoj-jar/judge/oj-judge.jar
rm ../flyoj-jar/system/oj-system.jar

copy ../../../oj-gateway/target/oj-gateway-1.0-SNAPSHOT.jar ../flyoj-jar/gateway/oj-gateway.jar
copy ../../../oj-modules/oj-judge/target/oj-judge-1.0-SNAPSHOT.jar ../flyoj-jar/judge/oj-judge.jar
copy ../../../oj-modules/oj-friend/target/oj-friend-1.0-SNAPSHOT.jar ../flyoj-jar/friend/oj-friend.jar
copy ../../../oj-modules/oj-job/target/oj-job-1.0-SNAPSHOT.jar ../flyoj-jar/job/oj-job.jar

copy ../../../oj-modules/oj-system/target/oj-system-1.0-SNAPSHOT.jar ../flyoj-jar/system/oj-system.jar
pause