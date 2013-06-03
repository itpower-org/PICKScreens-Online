var ITPBootstrapBrand = function() {
  var selector = '#itp-brand';
  var preserve = false;
  this.init = function() {
    $(selector).mouseover(showLogo);
    window.setTimeout(hideLogo, 3000);
  }
  var showLogo = function() {
    if (!preserve) {
      $(selector).fadeTo("slow", 1.0);
      window.setTimeout(function() {
        hideLogo();
        preserve = false;
      }, 5000);
    }
  }
  var hideLogo = function() {
    $(selector).fadeTo("slow", 0.1);
  }
};