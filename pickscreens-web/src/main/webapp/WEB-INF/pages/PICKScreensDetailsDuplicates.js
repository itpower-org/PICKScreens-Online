var PICKScreensDetailsDuplicates = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  var homepage = new PICKScreensHome();
  var jqGridOptions = {
    colNames : [ 'Screen 1 (Condition Number)', 'Screen 2 (Condition Number)', 'Recipe'],
    colModel : [ {
      name : 'screen1_condition_number',
      index : 'screen1_condition_number',
      width : 200
    }, {
      name : 'screen2_condition_number',
      index : 'screen2_condition_number',
      width : 200
    }, {
      name : 'recipe_substances',
      index : 'recipe_substances',
      width : 200
    } ]
  };
  var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel);
  $('button[name=details-duplicates]').click(showDetails);

  function showJSONAsTable(response) {
    if (response.succ) {
      pickscreensJQGrid.showJSON(response.rows);
    } else {
      homepage.showFail('1305081201');
    }
  }
  
  function showFail() {
    homepage.showFail('1305081200');
  }
  
  function showDetails(e) {
    homepage.showSpinner();
    var request = {};
    request.screens = JSON.stringify(homepage.getCheckedScreensIds());
    $.getJSON('details-duplicates.json', request, showJSONAsTable).fail(showFail).complete(homepage.hideSpinner);
  }
};