# Base Image
FROM  ubuntu:16.04

# Maintainer
MAINTAINER  Amit Vashist <amitvashist7@gmail.com>

# Update the Repo
RUN  apt-get update

# Install Apache Packages
RUN apt-get install apache2 -y 

# Add the Index File
COPY amit.abc /var/www/html/info.html
COPY centos-release /var/www/html/centos.html

# Expose the Service 
EXPOSE 80

# Bring the Apache UP
CMD ["/usr/sbin/apache2ctl", "-D", "FOREGROUND"]
