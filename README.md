# Popular Movies (Part 2)

### Overview

The purpose of this project is to build upon the [Popular Movies (Part 1)](https://github.com/vrendina/uda-popular-movies-1) application and demonstrate some more advanced Android development techniques. The application pulls data about popular or top rated movies from the Movie Database API (https://www.themoviedb.org/documentation/api) and presents the movies as a grid of posters. The user can select a poster to be taken to a detail activity where additional information about the movie is presented. A ContentProvider backed by a SQLite database stores favorite movies for offline access.  

### Libraries

* [Retrofit 2](https://github.com/square/retrofit)
* [RxJava 2](https://github.com/ReactiveX/RxJava) 
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Butter Knife](https://github.com/JakeWharton/butterknife)
* [Picasso](https://github.com/square/picasso)
* [Stetho](https://github.com/facebook/stetho)


### Architecture

To appropriately manage Android lifecycle changes a Model View Presenter (MVP) pattern was implemented. The MVP pattern was implemented with a singleton class `PresenterManager` which retains references for any presenters that may still be needed. This strategy allows any long running operations to persist through typical lifecycle changes (i.e. screen rotation). Inspiration for this pattern was taken from Brad Campbell's blog [post](http://blog.bradcampbell.nz/mvp-presenters-that-survive-configuration-changes-part-1/).

### Screenshots

![Grid](screenshots/grid.jpg?raw=true) ![Details](screenshots/details.jpg?raw=true)

### Usage
This application requires an api key for The Movie Database. A new string resource file `key.xml` should be created in the `res/values` folder. The value of `API_KEY` should be set to your API key assigned by The Movie Database.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="API_KEY">[insert MovieDB API key here]</string>
</resources>
```
