var PICKScreensHome = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  var jqGridOptions = {
    colNames : [ 'Name', 'Recipes', 'Download' ],
    colModel : [ {
      name : 'name',
      index : 'name',
      width : 250
    }, {
      name : 'recipes',
      index : 'recipes',
      sortable: true,
      sorttype : 'int',
      width : 50
    }, {
      name : 'download',
      index : 'download',
      width : 70
    } ],
    options : {
      rowNum : screens.length,
      pager : null,
      caption : 'Screens Available',
      multiselect : true,
      onSelectRow : function() {
        PICKScreensHome().actionOnSelectionChanged();
      },
      onSelectAll : function() {
        PICKScreensHome().actionOnSelectionChanged();
      },
      shrinkToFit : true,
      width : $('#screenWrapper').width()
    }
  };

  var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel, jqGridOptions.options);

  this.actionOnSelectionChanged = function() {
    updateCount();
    showHideSelectionDependencies();
    updateCookie();
  };

  var updateCount = function() {
    var countChecked = PICKScreensHome().getCheckedScreensIds().length;
    var countAvailable = $("#screens tr.ui-widget-content").length;
    var word = countChecked > 1 ? 'Screens' : 'Screen';
    $('.selectionCount').html(countChecked);
    $('.screenSingPlur').html(word);
    $('.screensAvailable').html(countAvailable);
    
    // set cross table button disabled
    var disabled = countChecked < PICKScreensConfig.ENABLE_MIN_SCREENS || countChecked > PICKScreensConfig.ENABLE_MAX_SCREENS;
    $('.disableOnMinMaxScreens').attr('disabled', disabled);
  };
  var updateCookie = function() {
    $.cookie("screens", JSON.stringify(PICKScreensHome().getCheckedScreensIds()), { expires: 300 })
  }
  var showHideSelectionDependencies = function() {
    $('.ifNothingSelected, .onlyIfSelected').hide();
    if (PICKScreensHome().getCheckedScreensIds().length > 0) {
      $('.onlyIfSelected').show();
    } else {
      $('.ifNothingSelected').show();
    }
  };
  
  function getDownloadLink(screen) {
    return $('<div/>').append($('<a/>', {href : 'download.xls?id=' + screen.id}).html('download')).html();
  }

  this.init = function() {
    $(screens).each(function(i, screen) {
      screen.download = getDownloadLink(screen);
    });
    pickscreensJQGrid.showJSON(screens, 'screens');
    updateCount();
    showHideSelectionDependencies();
    // set selection from cookie
    if(typeof($.cookie("screens")) != 'undefined') {
      var screenids = JSON.parse($.cookie("screens"));
      $(screenids).each(function(i, screenid){
        $("#screens").jqGrid('setSelection',screenid); 
      });
    }
  };

  this.showResult = function(content) {
    $('.hideOnResult').hide();
    $('#page_result .content').html(content);
    location.hash = 'result';
  };

  this.showFail = function(id) {
    this.showResult('<h2>Error :-(</h2><div>Please report error id: ' + id + '</div>');
  };

  this.getCheckedScreensIds = function() {
    return $("#screens").jqGrid('getGridParam', 'selarrrow');
  };

  this.hideSpinner = function()
  {
    $("body").removeClass("loading");
  }
  this.showSpinner = function()
  {
    $("body").addClass("loading");
  }

};