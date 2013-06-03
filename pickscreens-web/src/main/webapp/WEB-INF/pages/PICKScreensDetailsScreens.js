var PICKScreensDetailsScreens = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  var homepage = new PICKScreensHome();
  var jqGridOptions = {
    colNames : [ '#', 'Screen', 'Condition Number', 'Salt 1', 'Salt 2', 'Buffer', 'Precipitant 1', 'Precipitant 2' ],
    colModel : [ {
      name : 'id',
      index : 'id',
      width : 30,
      sortable: true,
      sorttype : 'int'
    }, {
      name : 'screen_name',
      index : 'screen_name',
      width : 150
    }, {
      name : 'condition_number',
      index : 'condition_number',
      width : 20,
      sorttype : 'int'
    }, {
      name : 'salt1',
      index : 'salt1',
      width : 150
    }, {
      name : 'salt2',
      index : 'salt2',
      width : 150
    }, {
      name : 'buffer',
      index : 'buffer',
      width : 150
    }, {
      name : 'precipitant1',
      index : 'precipitant1',
      width : 150
    }, {
      name : 'precipitant2',
      index : 'precipitant2',
      width : 150
    } ]
  };
  var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel);
  $('button[name=details-screens]').click(showDetails);

  function showJSONAsTable(response) {
    if (response.succ) {
      pickscreensJQGrid.showJSON(response.rows);
    } else {
      homepage.showFail('1304231234');
    }
  }
  
  function showFail() {
    homepage.showFail('1304231233');
  }
  
  function showDetails(e) {
    homepage.showSpinner();
    var request = {};
    request.screens = JSON.stringify(homepage.getCheckedScreensIds());
    $.getJSON('details-screens.json', request, showJSONAsTable).fail(showFail).complete(homepage.hideSpinner);
  }
};