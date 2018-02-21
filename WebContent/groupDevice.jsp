<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Device view</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<link rel="stylesheet" type="text/css" href="theme/Theme1/main.css">
<script type="text/javascript" src="theme/d3.v3.min.js"></script>
<script type="text/javascript" src="theme/index.js"></script>
</head>
<style>
.node {
	//cursor: pointer;
}

.node circle {
  fill: #fff;
  stroke: steelblue;
  stroke-width: 2px;
}

.node text {
  font: 12px sans-serif;
}

.link {
  fill: none;
  stroke: #ccc;
  stroke-width: 2px;
}

.d3-tip {
  line-height: 1;
  font-weight: bold;
  padding: 12px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  border-radius: 10px;
}

</style>
<%@ include file="theme/Theme1/menu_bar.jspf"%>
<body onload="createMenu('Group Map')">
<div id="area" style="width: 1024px;margin: auto;">
<%@ include file="theme/Theme1/prefix_s.jspf"%>
<script>

function viewDevice(name){
	$.ajax({
		   type: "POST",
		   data: {name:name},
		   url: "Device?method=groupDevice",
	       success: function(data){
	    	   if(data.length == 0){
	    		   alert("This group doesn't have devices.");
	    		   window.close();
	    	   }
	    	   $.each(data, function(key, json_data) {
					 tree(json_data);  
				   });
	       },
	});
}

function tree(data){
	var treeData = [data];
	
	
	// ************** Generate the tree diagram	 *****************
	var margin = {top: 20, right: 120, bottom: 20, left: 80},
		width = 1000 - margin.right - margin.left,
		height = 300 - margin.top - margin.bottom;
		
	var i = 0,
		root;
	
	var tree = d3.layout.tree()
		.size([height, width]);
	
	var diagonal = d3.svg.diagonal()
		.projection(function(d) { return [d.y, d.x]; });
	
	var tip = d3.tip()
		.attr('class', 'd3-tip')
		.direction('e')
		.offset([-30, 10])
		.html(function(d) { return "" + d.aliasName + ""; });
	
	var svg = d3.select("#area").append("svg")
		.attr("width", width + margin.right + margin.left)
		.attr("height", height + margin.top + margin.bottom)
	    .append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	svg.call(tip);
	
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
	   .attr("r", 20)
	   .style("fill", "#fff")
	   .style("stroke-width", function(d) { return d.circleColor == "yellow" ||  d.circleColor == "red" ? "3px" : "2px"; })
	   .style("stroke", function(d) { return d.circleColor; });
	  
	  nodeEnter.append("image")
	   .attr("xlink:href", function(d) { return d.icon; })
	   .attr("x", "-12px").attr("y", "-12px").attr("width", "24px").attr("height", "24px")
	   .on('mouseover', tip.show)
       .on('mouseout', tip.hide);
	
	  nodeEnter.append("text")
	   .attr("y", "-2.2em")
       .attr("text-anchor", "middle")
	   .text(function(d) { return d.name; })
	   .style("font-weight", function(d) { return d.circleColor.length > 0 ? "bold" : ""; })
	   .style("fill", function(d) { return d.circleColor; });
	
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
<c:out value="<script type='text/javascript'>$(document).ready(viewDevice('${param.name}'));</script>" escapeXml="false"></c:out>
</div>
</body>
</html>