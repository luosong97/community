#生成mybaties
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate

#打包
mvn package -Dmaven.test.skip=true -Ptest