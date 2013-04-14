# Overview

Decdnorator is a Java servlet filter that does page decoration for files that are suitable for placing in a CDN.  It tokenizes full HTML pages using block markers (like Python's [Flask](http://flask.pocoo.org/docs/patterns/templateinheritance/)) instead of page parsing (like [Sitemesh](sitemesh.org)).  Multiple stages of decoration are possible, as is a rudimentary include mechanism.  Multiple blocks are extractable (say, main-content, title, javascript) and insertable.  The block markers are HTML/JavaScript friendly though, so pages could be loaded into a browser raw, and still function.

Whereas Sitemesh decorates arbitrary pages, including dynamic ones with user data in them, Decdnorator is only for static (unchanging) pages that could fill a CDN.  It is really to aid developers separating resources, and avoiding cut and paste .... for Client-Side MVC technologies, where you could break a 'Single Page Application' into a flow of smaller pages, all still sharing code and aesthetics. 

# Example of use

Build and deploy it yourself:

        mvn clean install
        cd example
        mvn jetty:run-war

Then open a browser, go to [http://localhost:8080](http://localhost:8080) and click around.  Note that I've engineered two views of the same page - one with secondary decoration, and one without.

There are two forms of the same page there, the source files are:

* Base page: [has_two_angular_controllers.html](https://github.com/paul-hammant/decdnorator/blob/master/jar/src/test/resources/has_two_angular_controllers.html)

* A Decorator: [adds_a_header_and_footer_for_mixed_example.html](https://github.com/paul-hammant/decdnorator/blob/master/jar/src/test/resources/adds_a_header_and_footer_for_mixed_example.html)

There are four extract/insert variables here: GreetJs, ListJs, Greet and List. There are contrived for this example, and you'd normally use more general names as you're sharing decorators for part of a larger experience.

For your convenience, I've taken a copy of those pages, and uploaded them statically to GitHub Pages [http://paul-hammant.github.com/decdnorator](http://paul-hammant.github.com/decdnorator), although "?" has been changed to "@" to make it a file name and a ".html" tacked on for one file, as the item would not be statically serve-able without that. 

# Using it in your own Web Application

The Filter is something you'll write yourself, but use our example as a starting point : [https://github.com/paul-hammant/decdnorator/blob/master/example/src/main/java/com/paulhammant/decdnorator/example/ExampleFilter.java](https://github.com/paul-hammant/decdnorator/blob/master/example/src/main/java/com/paulhammant/decdnorator/example/ExampleFilter.java)
 
Our example has our static page source in WEB-INF.  At least the pages that would be decorated or are decorators. You could to co-mingle those files in the root along with other resources (CSS, images, non-decoration HTML) if you prefer.

# API

See our unit tests:

https://github.com/paul-hammant/decdnorator/blob/master/jar/src/test/java/com/paulhammant/decdnorator/RendererTest.java

# Not implemented (patches accepted).

* Explicit CDN filling or purging.
* Built-in Caching
* Compatibility with un-exploded WAR file deployment (WebSphere)
