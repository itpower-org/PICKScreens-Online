<# 
build a jar of the crontab with dependencies and execute it with current configuration
author: daniel.oltmanns@it-power.org
since: 03/22/2013
#>

function Execute-Cronjob() {
  <#
  .SYNOPSIS
  execute the cronjob for pickscreens
  .DESCRIPTION
  execute the cronjob for pickscreens
  .EXAMPLE
  Execute-Cronjob
  Output:
  <?php
  class Earth {
  /** The one and only Earth */
  [...]
  .EXAMPLE
  Get-PhpSingleton Earth
  short for Get-PhpSingleton -ClassName Earth
  .EXAMPLE
  Get-PhpSingleton
  using SingletonClass as default name
  <?php
  class SingletonClass {
  /** The one and only SingletonClass */
  [...]
  #>
  mvn clean install
  cd pickscreens-cron
  mvn clean assembly:assembly
  java -jar .\target\pickscreens-jar-with-dependencies.jar
}
Execute-Cronjob