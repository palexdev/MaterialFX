#### This module is intended to be used in two particular occasions:

1) SceneBuilder: all this module does is producing a Jar that contains all the dependencies of MaterialFX,
   this makes usage in SceneBuilder really easy as all you have to do is include this artifact instead of importing
   all the modules one by one
2) Non-modular projects. Usage in modular projects is discouraged, and you will probably encounter problems otherwise.
   The Jar building process removed the 'module-info.java' file from the modules, so it cannot be used anymore with
   the Java Module System as intended.