<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<div class="row">
	<h1 class="span9">Help</h1>
</div>
<div class="row">
	<div class="span6">
		<h2>About</h2>
		<p>
			<span class="pointOut">PICKScreens Database Online</span> is for
			comparing the crystallization screen formulations. It allows finding
			identities among screens and thereby consciously choosing those that 
			have the highest or the lowest redundancy according to your needs.
		</p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h2>Using PICKScreens Online</h2>
		<p>
		  Prior to starting your search, select one or more screens shown in the table on the left-hand side.
	  </p>
	</div>
	<div class="span4">
		<h2>Examples</h2>
		<p>
		Show recipes / screens having substances with&nbsp;…
		</p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Search substances</h4>
	  <p>
			Any input in the search field on the upper right side is interpreted as a search for a substance.
			Given you want to search for sugars (not a specific one), enter the appropriate ending which appears to be
			"ose" in this case. As a result, all recipes containing a substance which name matches (at least partly) "ose"
			will be displayed.
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;“ium” in the name (finds “sodium chloride”, “ammonium phosphate” …):<br /><code>ium</code></p>
		<p class="example">…&nbsp;"meth" in the name (finds “methylene”, “methyl”, "methane"…):<br /><code>meth</code></p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Search amounts</h4>
		<p>
			You can search for substances with a minimum or maximum amount (of whatever substance or unit).<br />
			Use <code>=</code> or one of these inequality symbols <code>&lt;</code>, <code>&lt;=</code>, <code>&gt;</code> or <code>&gt;=</code> followed by a number to compare an amount in the recipes with.<br />
			The number can be an integer (<code>3</code>) or a floating point number (<code>3.0</code>).
			Use <code>&lt;=</code> and <code>&gt;=</code> whether the numbers provided should be included.
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;less then 5:<br /><code>&lt;5</code></p>
		<p class="example">…&nbsp;greater than or equal to &nbsp;1.2:<br /><code>&gt;=1.2</code></p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Search between amounts</h4>
		<p>
		    It is also possible to search for a varying amount as a range between two numbers. 
			Simply combine the inequality symbols above to search recipes having substances 
			between two amounts.
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;an amount between 5 and 6:<br /><code>&gt;5 &lt;6</code></p>
		<p class="example">…&nbsp;an amount between and including at least 5 and at most 6:<br /><code>&gt;=5 &lt;=6</code></p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Search for amount and unit</h4>
		<p>
			If you are looking for recipes using substances with an amount of a specific unit, append the unit abbreviation to your search-term.
		</p>
		<p>
		  Hint: The application does not know any kind of unit conversion (e.g. 1000&nbsp;ml&nbsp;=&nbsp;1&nbsp;l). Please provide the dimensions
		  so that the unit abbreviation in your search-term will most probably match the unit in the actual recipe (1000ml will probably be written as 1l, in this case please enter "1l").
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;exactly&nbsp;5&nbsp;ml:<br /><code>=5ml</code></p>
		<p class="example">…&nbsp;careful! This will not work because - leaving the units apart, so does the application as mentioned in the hint to the left - 
						any value found in the recipe cannot be less than 3 but greater than 8 at a time:<br /><code style="text-decoration:line-through">&lt;3cl &gt;8ml</code>
		</p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Search many substances</h4>
		<p>
			Given you want to search for recipes containing two or more specific substances, use <code>&amp;</code> to concatenate arguments. The application will 
			search for recipes containing at least all of the substances which names match (at least partly) your search-term. 
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;recipes with two or more substances, one of them contains "glycerol" and the another contains "hepes" in their names:<br /><code>glycerol &amp; hepes</code></p>
	</div>
</div>
<div class="row">
	<div class="span6">
		<h4>Combined searches</h4>
		<p>
			Combining the above with specific amounts and units added to the substance' name or part of the name will apply a detailed search for certain recipes.
		</p>
	</div>
	<div class="span4 sidebar">
		<p class="example">…&nbsp;exactly 5&nbsp;ml of something with “mmoni“ in the name and water between 1&nbsp;ml and 2&nbsp;ml and 0.2&nbsp;%&nbsp;(or ml,&nbsp;l,&nbsp;g&nbsp;…) potassium chloride:<br /><code>=5ml mmoni &amp; &lt;=2ml &gt;1 water &amp; =0.2 potassium chloride</code></p>
	</div>
</div>