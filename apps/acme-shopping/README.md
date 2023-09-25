# Shopping Service

[![gcr.io](https://img.shields.io/badge/gcr.io-stable-green?style=flat-square)](https://console.cloud.google.com/gcr/images/vmwarecloudadvocacy/GLOBAL/acmeshop-front-end@sha256:50407894d66065e846819f2d0060bce8fab8be433cbcf973d7dce2f27eeb2390/details?tab=info)

This fork is for demonstrating [Spring Cloud Gateway for kubernetes](https://docs.vmware.com/en/VMware-Spring-Cloud-Gateway-for-Kubernetes/index.html). Compared to the original repo, this fork doesn't have the node backend or anything dynamic. It's merely a static frontend app that relies on Spring Cloud Gateway to proxy all the outbound requests. The modified images is published at: [springcloudservices/fitness-frontend](https://hub.docker.com/r/springcloudservices/fitness-frontend).

> A front-end service, because how can you shop without seeing?

The Front-End service is part of the [ACME Fitness Shop](https://github.com/vmwarecloudadvocacy/acme_fitness_demo). The goal of this specific service is to serve as the front-end that presents the content.

## Prerequisites

There are different dependencies based on whether you want to run a built container, or build a new one.

### Build

* [Docker](https://www.docker.com/docker-community)

### Run

* [Docker](https://www.docker.com/docker-community)
* Web browser: Tested on the latest versions of Chrome and Safari (use developer tools in your browser to look at console logs for client side)

## Installation

### Docker

Use this command to pull the latest tagged version of the shipping service:

```bash
docker pull springcloudservices/fitness-frontend:latest
```

To build a docker container, run `docker build . -t springcloudservices/fitness-frontend:<tag>`.

The images are tagged with:

* `latest`: denotes the most recently pushed image. It may not be appropriate for all use cases

## Code Structure

### public/

* Contains all the html file
* every html file has a <script> tag under which the AJAX scripts are added
* follow the index.html for some of the naming conventions. Especially for the Top navigation bar and the footer
* make changes as necessary for your service (like redirecting to another page, loading a different html etc)

### public/js/client.js

* Contains all the js functions for handling certain front end actions

## Available users

There are four pre-created users loaded into the database:

| User   | Password   |
|--------|------------|
| eric   | `vmware1!` |
| dwight | `vmware1!` |
| han    | `vmware1!` |
| phoebe | `vmware1!` |

* You MUST login as one of the users mentioned above to access all the pages in the application
* The current user service will set a cookie ```logged_in``` in the browser. This cookie contains the User ID returned from the user service
* The service uses JWT and sets 2 cookies - ```logged_in``` and ```refresh_token```

## License

See the [LICENSE](./LICENSE) file in the repository
