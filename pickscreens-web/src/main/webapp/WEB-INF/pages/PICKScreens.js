var PICKScreens = function() {
  
  if(arguments.callee._singletonInstance) return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  this.init = function() {
    var home = new PICKScreensHome();
    home.init();

    var brand = new ITPBootstrapBrand();
    brand.init();

    // set menu items
    $(window).on('hashchange', changeNavigation);
    changeNavigation();
    
    window.setTimeout(function(){
      $('div.row.alertlabels').hide('slow');
    }, 5000);
  }
  var changeNavigation = function(e) {
    var page = 'home';
    var tmp;
    // check, if navigation is valid and exists
    if (location.hash.length > 1) {
      tmp = location.hash.substr(1);
      if ($('div#page_' + tmp).length == 1) {
        // navigation is valid and exists
        page = tmp;
      }
    }
    $('.nav li').removeClass('active');
    $('li#nav_' + page).addClass('active');
    $('.hideOn').show();
    $('.hideOn_' + page).hide();
    $('.showOnly').hide();
    $('.showOnly_' + page).show();
    $('div.page').hide();
    $('div#page_' + page).show();

    new PICKScreensDetailsScreens();
    new PICKScreensDetailsDuplicates();
    new PICKScreensCrossTable();
    new PICKScreensSubstances();
    new PICKScreensSearch();
  }
};