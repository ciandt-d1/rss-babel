FROM java:8
MAINTAINER Daniel Viveiros "viveiros@ciandt.com"

ADD build/libs/rss-babel-0.1.jar /usr/src/app/

EXPOSE 8080
EXPOSE 8081

CMD java -Denv=${ENV} -Dspring.profiles.active=${ENV} -jar /usr/src/app/rss-babel-0.1.jar