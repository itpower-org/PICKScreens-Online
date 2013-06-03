var PICKScreensSubstances = function() {

  if (arguments.callee._singletonInstance)
    return arguments.callee._singletonInstance;
  arguments.callee._singletonInstance = this;

  // construct it
  var homepage = new PICKScreensHome();
  var jqGridOptions = {
    colNames : [ 'Substance name', 'Synonyms' ],
    colModel : [ {
      name : 'substance_name',
      index : 'substance_name',
      width : 450
    }, {
      name : 'synonyms',
      index : 'synonyms',
      width : 450
    } ]
  };
  var pickscreensJQGrid = new PICKScreensJQGrid(jqGridOptions.colNames, jqGridOptions.colModel);
  $('button[name=substances]').click(getAndShowSubstances);
  function getAndShowSubstances(e) {
    homepage.showSpinner();
    $.getJSON('substances.json', {}, delegateResponse).fail(showFail).complete(homepage.hideSpinner);
  }
  function showFail() {
    homepage.showFail('1305061650');
  }
  function delegateResponse(response) {
    if (response && response.succ) {
      pickscreensJQGrid.showJSON(response.rows);
    } else {
      homepage.showFail('1305061651');
    }
  }
};