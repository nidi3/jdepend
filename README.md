## This repository is deprecated
### [code-assert](https://github.com/nidi3/code-assert) has the same functionalities as jdepend, but allows a lot more static code analysis. 


Changes from the original: [![Build Status](https://travis-ci.org/nidi3/jdepend.svg?branch=master)](https://travis-ci.org/nidi3/jdepend)

 - Fix bug in DependencyConstraint (https://github.com/clarkware/jdepend/issues/5)
 - Support Java 8 class files (https://github.com/clarkware/jdepend/issues/7)
 - Add hamcrest matchers
 - Use generics
 - Mavenize
 - New APIs for DependencyConstraint and PackageFilter
 - Parse generic signatures in class files

```

                            J D E P E N D     
 

  What Is It? 
  -----------
  
  JDepend traverses Java class and source file directories and 
  generates design quality metrics for each Java package. JDepend allows 
  you to automatically measure the quality of a design in terms of its 
  extensibility, reusability, and maintainability to effectively manage 
  and control package dependencies.


  How Do I Get It?
  ----------------

  The latest version of JDepend is available at 
  http://www.clarkware.com/software/jdepend.zip

  JDepend is also available on GitHub at:
  http://github.com/clarkware/jdepend


  Documentation
  -------------

  Documentation is available in HTML format, in the docs/ directory.
  For the installation and user manual, see docs/JDepend.html.
  For the API documentation, see docs/api/index.html.


  Support
  ---------

  If you have any questions, comments, enhancement requests, success
  stories, or bug reports regarding JDepend, please send them to
  mike@clarkware.com.


  Licensing
  ---------

  This software is licensed under the terms described in the file 
  named "LICENSE" in this directory.
  

  Thanks for using JDepend!

``