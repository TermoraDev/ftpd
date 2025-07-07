# Docker FTP Server

[https://github.com/TermoraDev/ftpd](https://github.com/TermoraDev/ftpd)

----

<p>Pull down latest version with docker:</p>

```shell
docker pull termoradev/ftpd:latest
```

**Often needing to run as** `sudo` , **e.g.** `sudo docker pull termoradev/ftpd:latest`

## Starting

```shell
docker run --rm -p 21:21 \
 -p 1000-1005:1000-1005 \
 -e USERS="admin|admin,foo|bar" \
 -e PASV_PORTS="1000-1005" \
 -e PASV_ADDRESS=10.211.55.2 \
 -v $(pwd)/data/:/app/data/ \
 termoradev/ftpd:latest
```

## Env

| Name         | Default Value  | Description                        |
|--------------|----------------|------------------------------------|
| USERS        | `admin\|admin` | Username and password              |
| PASV_PORTS   | `1024-65535`   | Default ports used in passive mode |
| PASV_ADDRESS | `127.0.0.1`    | Host address used in passive mode  |
