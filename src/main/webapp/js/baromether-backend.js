
(function ($) {
  
  $(document).ready(function(){
    $( ".close-click" ).click(function() {
      $( ".popup" ).fadeOut( "slow" );
    }); 
    $( ".view-click" ).click(function() {
      $( ".popup" ).fadeIn( "slow" );
    });  

  });

})(jQuery);