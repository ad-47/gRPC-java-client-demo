# gRPC-java-client-demo

### Project build & execution Steps:

**Note** : This project requires **JAVA version 8** to run. Please setup and configure JAVA version 8 prior to running and then run following below mentioned steps.

```
1) mvn clean
```

If you have not configured protocol buffer compiler in your POM as a build extension, then you would need to download protocol buffer compiler and set the compiler path in your system path. Then, run below commands for all .proto files in order to generate Java code from .proto files.

```
2) protoc --proto_path=src/main/proto --java_out="output_folder_path" ".proto file path"

	ex:- protoc --proto_path=src/main/proto --java_out=src/main/protogen src/main/proto/ProjectService.proto
```

Since current codebase has proto buffer compiler already setup in POM, no need to run the protoc command as above, You can straight continue to the following command

```
2) mvn package
	OR Run All JUnit Test case : mvn test
	OR Run single test using : mvn -Dtest=TestName test
	OR Exclude JUnit Test case with maven package then use : mvn package -Dmaven.test.skip=true
```

Run gRPC client using following command

```
3) mvn -DskipTests package exec:java -Dexec.mainClass=com.anand.grpc.App -Dexec.args=
	ex :
	- Dexec.args=0 (For add User)
	- Dexec.args=1 (For getting Users using streaming)
	- Dexec.args=any (For getting All Users put any other number as args)
```
