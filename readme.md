## 前置
### Step1. 安裝 `jdk 17`
```
// 下載 GraalVM：根據作業系統下載適當的版本（建議下載 GraalVM JDK 17，與 Spring Boot 3.x 相容）。
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/v23.0.1/graalvm-ce-java17-linux-amd64-23.0.1.tar.gz

// 解壓縮並設定環境變數：解壓縮下載的檔案並設定JAVA_HOME 和 PATH
tar -xzf graalvm-ce-java17-linux-amd64-23.0.1.tar.gz
export GRAALVM_HOME=$PWD/graalvm-ce-java17-23.0.1
export PATH=$GRAALVM_HOME/bin:$PATH
export JAVA_HOME=$GRAALVM_HOME

//驗證安裝：檢查GraalVM是否安裝成功
java -version
```
### Step2. 由 `spring.io` 產生專案模板
```
// 可由`https://start.spring.io/`產生變下載，或由以下方式自定義url下載模板
curl -o demo-app.zip "https://start.spring.io/starter.zip?type=maven-project&language=java&bootVersion=3.4.1&baseDir=demo-app&groupId=com.example&artifactId=demo-app&version=1.0.0-SNAPSHOT&name=DemoApp&description=A%20demo%20Spring%20Boot%20application.&packageName=com.example.demo&dependencies=web,devtools,actuator"

// 解壓 ZIP 文件
unzip demo-app.zip

```

### Step3. 於專案pom.xml加入Camel BOM
進行Camel依賴版本管理
```
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-spring-boot-bom</artifactId>
    <version>${camel-version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```
### Step4. 加入yaml路徑掃描
於專案下/src/main/resources/application.properties內加入設定
```
camel.main.routes-include-pattern=file:/mnt/*.yaml
```

### Step5. 安裝kaoto協助開發yaml dsl
於vscode extension內搜尋`kaoto`

### Step6. 於pom.xml內加入打包配置
#### 如要打包成jar則於plugins添加以下內容
```
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
    <plugin>
        <groupId>com.spotify</groupId>
        <artifactId>dockerfile-maven-plugin</artifactId>
        <version>1.4.13</version>
        <configuration>
            <repository>tomdeltawork/userdata-project-springboot-camel</repository>
            <tag>darunjar.v10.0.0</tag>
            <dockerfile>Dockerfile.darunjar</dockerfile>
            <buildArgs>
                <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
            </buildArgs>
        </configuration>
    </plugin>  
```
#### 如要打包成native則於plugins添加以下內容
```
    <plugin>
        <groupId>org.graalvm.buildtools</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <executions>
            <execution>
                <goals>
                    <goal>compile</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <resources>
                <includes>
                    <include>routes/*.yaml</include>
                </includes>
            </resources>
        </configuration>
    </plugin>           
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <image>
                <builder>paketobuildpacks/builder:base</builder>
                <runImage>ubuntu:20.04</runImage>
                <name>tomdeltawork/userdata-project-springboot-camel:v8.0.0</name>
            </image>
        </configuration>
    </plugin>
```

## 運行
執行以下指令
```
./mvnw package -Pnative -DskipTests
```

## 打包成各種docker image
### jar
```
./mvnw clean package dockerfile:build -DskipTests
```
### native
```
./mvnw spring-boot:build-image -Pnative -DskipTests
```