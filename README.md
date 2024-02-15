# WARP-narrowcast

This application is used to display current office locations of colleagues that are registered
in [WARP: Workspace Autonomous Reservation Program](https://github.com/sebo-b/warp) on a big
screen and to help colleagues find each other easily.

It offers the following 'views' of the current reservations for projections on a big screen:

* Which rooms are empty today?
* Which employees occupy which office at this moment
* Which employees are currently in the office and which office can you find them?

There is also a more detailed table with all reservations of the current day.
Furthermore, per room a separate URL is available that shows the current occupants of the
office in the style of a "name sign".

Additionally, the application also supports API endpoints that provide the same information
as JSON. These API endpoints can be easily enabled or disabled.

The application does not have any authentication mechanisms, as it is assumed access
to the application can be limited on a network level (e.g. only make it available to
narrow-casting devices or on the organization's intranet).
However, it should be relatively easy to secure
the application using [Spring Security](https://spring.io/projects/spring-security)
in deployment contexts where network-level access control is insufficient.

In order to limit development time the HTML-templates are kept relatively simpled
and are only minimally styled, with some help from the [Materialize project](https://materializecss.com/).
As we discovered after deployment that the narrow-casting software used by our university
to display websites on big screen still uses the `MSHTML` rendering engine,
some last minute Internet Explorer support  was added, mostly by some alternative CSS-rules.

## How does it work?

Internally, all data processing is performed using three types of data,
bookings, seats, and users. This data is read from the WARP application. 

To determine unique rooms/offices, the seat names are analyzed, where it is assumed
that the last occurence of a `_` or `-` symbol separates the seat-indentifier from
the office identiefier. So a seat named `ET-10_a` is assumed to be in office `ET-10`
with seat identifier `a`.
A seat name `ET-15` would be assumed to be in office `ET` with seat identifier `15`.
Hence, it is important the seat names defined in WARP always contain both the name
of the office and an identifier for the seat, even in case of single seat offices. 

Using the convenient features of the Java Time API's, the relevant bookings for the
current moment and/or the current are then determined, to obtain the relevant information.

## Architecture

The application uses Spring Boot, together with the following modules:

* [Spring Data JPA](https://spring.io/projects/spring-data-jpa) for database access
* Spring Web for the application
* [Thymeleaf](https://www.thymeleaf.org/) for processing HTML-templates

Classes for the booking, seat and user data can be found in the 
[`entities` package](src/main/java/nl/eur/ese/ei/warp/narrowcast/entities) package.
The [`repos` package](src/main/java/nl/eur/ese/ei/warp/narrowcast/repos) package
contains interfaces with query methods that can read the relevant data from
the WARP database.

The methods used to convert raw booking, seat and user data into useful information are
performed by a service component of which the interface is defined as
[`RoomService`](src/main/java/nl/eur/ese/ei/warp/narrowcast/service/RoomService.java)
.
Two implementations are provided in this application: the
[`DatabaseRoomService`](src/main/java/nl/eur/ese/ei/warp/narrowcast/service/DatabaseRoomService.java)
reads the data from a WARP database using the earlier mention repository components,
while the
[`MockRoomService`](src/main/java/nl/eur/ese/ei/warp/narrowcast/service/MockRoomService.java)
implementations works with randomized data for demonstration purposes and does not
depend on an actual WARP installation.
Both service components extend the abstract class
[`RoomServiceAdapter`](src/main/java/nl/eur/ese/ei/warp/narrowcast/service/RoomService.java)
which does most of the algorithmic heavy lifting in figuring out the relevant information
for a particular request.

It is assumed that a single `RoomService` component is active while the application is running,
which can be controlled using Spring profiles.
This service component is used by the
[controllers](src/main/java/nl/eur/ese/ei/warp/narrowcast/controllers)
where the
[`ApiController`](src/main/java/nl/eur/ese/ei/warp/narrowcast/controllers/ApiController.java)
handles API requests (if the `api` profile is active), and the
[`WebController`](src/main/java/nl/eur/ese/ei/warp/narrowcast/controllers/WebController.java)
handles web-page requests and renders HTML pages with relevant information.

The HTML templates used to display various types of information are in the
[`templates`](src/main/resources/templates)
directory. These are styled with the light [Materialize project](https://materializecss.com/)
and a [custom static stylesheet](src/main/resources/static/style.css) 

Finally, the
[`ConfigProperties`](src/main/java/nl/eur/ese/ei/warp/narrowcast/ConfigProperties.java)
class defines custom properties used to convert between timezones.
The
[`WarpNarrowcastApplication`](src/main/java/nl/eur/ese/ei/warp/narrowcast/WarpNarrowcastApplication.java)
is the main entrypoint of the application.

## How to use it?

As this is a [Spring Boot](https://spring.io/projects/spring-boot) application you need to have a recent
version of Java available. The build tool used is Maven, so having that is also recommended
(although many professional Java IDE's have Maven included).

```
mvn spring-boot:run 
```

This will start up the application with the current settings in `application.properties`.
Once the application has started, a demonstration using mock-data can be viewed at
[http://localhost:8080](http://localhost:8080)

In order to compile a packaged version for deployment, the following command can be used:

```
mvn package
```

This will generate a *fat-jar* in the `/target` directory that can be used for deployment
on an application server.

In actual deployment, it is needed to put a copy of 
[`application.properties`](application.properties)
and edit the configuration to disable the `mock` profile
and contain the relevant database credentials for the WARP-instance.

## Can this application be adapted to work with other backends than WARP?

In principle, all that would be needed is to have another implementation of the `RoomService`
interface that obtains relevant booking, seat and user data.
Spring's dependency injection could then be used to ensure that the `ApiController` and `WebController`
make use of this new implementation to serve the data of another service.

The Spring framework
provides a number of [clients and methods](https://docs.spring.io/spring-framework/reference/web/webmvc-client.html)
to talk to external web-services. So while some work would be needed to make translate
data from the external service to fit the data representation of this application,
the amount of work could be reasonable depending on the external web-application.

## License and Contact

This application is currently licensed under the GNU AFFERO GENERAL PUBLIC LICENSE version 3.
The rationale is that commercial companies should not too easily benefit from this work
without further discussion.
For further inquiries about the state of this application, please contact
[Paul Bouman](https://paulbouman.nl/)
