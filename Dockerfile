FROM gcr.io/distroless/java
COPY target/universal/stage/lib/* /app/lib/
COPY target/universal/stage/conf/ /app/conf/
WORKDIR /app
EXPOSE 9000
ENTRYPOINT ["java"]
CMD ["-Duser.dir=/app", "-Dconfig.file=conf/production.conf", "-cp", "conf/:lib/*", "play.core.server.ProdServerStart"]