<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script type="text/javascript" src="theme/d3.v3.min.js"></script>
<style>

.node circle {
  fill: #fff;
  stroke: steelblue;
  stroke-width: 3px;
}

.node text {
  font: 14px sans-serif;
}

.link {
  fill: none;
  stroke: #ccc;
  stroke-width: 2px;
}

.hyper {
    cursor: pointer;
    text-decoration: underline;
}

.hyper:hover {
    cursor: pointer;
    text-decoration: none;
}

</style>
<body style="height:600px; overflow-y:hidden;">
<script>

$(document).ready(function(){
	$.ajax({
		   type: "POST",
		   url: "Device?method=groupTopology",
	       success: function(data){
	    	   tree(data);
	    	   
	       },
 	});
});

function tree(data){
	var treeData = [data];
	
	
	// ************** Generate the tree diagram	 *****************
	var margin = {top: 20, right: 120, bottom: 20, left: 120},
		width = 10000 - margin.right - margin.left,
		height = 500 - margin.top - margin.bottom;
		
	var i = 0,
		root;
	
	var tree = d3.layout.tree()
		.size([height, width]);
	
	var diagonal = d3.svg.diagonal()
		.projection(function(d) { return [d.y, d.x]; });
	
	var svg = d3.select("body").append("svg")
		.attr("width", width + margin.right + margin.left)
		.attr("height", height + margin.top + margin.bottom)
	    .append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	root = treeData[0];
	root.x0 = height / 2;
	root.y0 = 0;
	  
	update(root);
	
	//d3.select(self.frameElement).style("height", "500px");
	
	function update(source) {

	  // Compute the new tree layout.
	  var nodes = tree.nodes(root).reverse(),
	   links = tree.links(nodes);
	
	  // Normalize for fixed-depth.
	  nodes.forEach(function(d) { d.y = d.depth * 120; });
	
	  // Declare the nodesâ€¦
	  var node = svg.selectAll("g.node")
	   .data(nodes, function(d) { return d.id || (d.id = ++i); });
	
	  // Enter the nodes.
	  var nodeEnter = node.enter().append("g")
	   .attr("class", "node")
	   .attr("transform", function(d) { 
	    return "translate(" + d.y + "," + d.x + ")"; });
	
	  nodeEnter.append("circle")
	   .attr("r", 10)
	   .style("fill", "#fff")
	   .style("stroke-width", function(d) { return d.circleColor == "yellow" ||  d.circleColor == "red" ? "3px" : "2px"; })
	   .style("stroke", function(d) { return d.circleColor; });
	
	  nodeEnter.append("text")
	   .attr("y", "-1.2em")
       .attr("text-anchor", "middle")
	   .text(function(d) { return d.name; })
	   .style("font-weight", function(d) { return d.circleColor.length > 0 ? "bold" : ""; })
	   .style("fill", function(d) { return d.circleColor; })
	   .attr("class", "hyper").on("click", clack);

	  function clack(d) {
    	if(d.url != null){
    		window.open(d.url);
    	}
      }
	
	  // Declare the linksâ€¦
	  var link = svg.selectAll("path.link")
	   .data(links, function(d) { return d.target.id; });
	
	  // Enter the links.
	  link.enter().insert("path", "g")
	   .attr("class", "link")
	   .attr("d", diagonal);
	
	}
}

</script>
</body>
</html>