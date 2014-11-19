$(document).bind("mobileinit", function()
{
  $.support.cors = true;
  $.mobile.allowCrossDomainPages = true;
  //$.mobile.defaultPageTransition = 'slide';
  $.mobile.defaultPageTransition = 'none';
  $.mobile.defaultDialogTransition = 'none';
  $.mobile.ignoreContentEnabled=true;
});