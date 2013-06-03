var TextFieldValueControl = function(textFieldSelector, defaultValue) {
  if (!defaultValue)
    throw 'need a default value';
  
  $(textFieldSelector).val(defaultValue);
  
  var setValue = function() {
    var val = jQuery.trim($(textFieldSelector).val());
    if (val == '') {
      $(textFieldSelector).val(defaultValue);
    } else if (val == defaultValue) {
      $(textFieldSelector).val('');
    }
  }
  
  /** return the trimmed value as a string or false if text field does not have an input or is the default value */
  this.getValue = function() {
    var result = false;
    var val = jQuery.trim($(textFieldSelector).val());
    if (val != '' && val != defaultValue) {
      result = val;
    }
    return result;
  }
  
  this.init = function() {
    $(textFieldSelector).focus(setValue).blur(setValue);
  }
};