<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Path Status</title>
<script type="text/javascript" src="javascript/jquery-1.10.2.js"></script>
<script src="http://d3js.org/d3.v3.min.js"></script>

<script type="text/javascript">
	function update(root) {
		console.log(root);

		if (root.name.indexOf("Please") != -1) {
			$("#message").html(root.name);
		} else {
			// ************** Generate the tree diagram	 *****************
			var margin = {
				top : 50,
				right : 10,
				bottom : 10,
				left : 50
			}, width = 1024 - margin.right - margin.left, height = 100 - margin.top - margin.bottom;

			var i = 0;

			var tree = d3.layout.tree().size([ height, width ]);

			var diagonal = d3.svg.diagonal().projection(function(d) {
				return [ d.y, d.x ];
			});

			var svg = d3.select("body").append("svg").attr("width", width + margin.right + margin.left).attr("height", height + margin.top + margin.bottom).append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			// Compute the new tree layout.
			var nodes = tree.nodes(root).reverse(), links = tree.links(nodes);

			// Normalize for fixed-depth.
			nodes.forEach(function(d) {
				d.y = d.depth * 120;
			});

			// Declare the nodes…
			var node = svg.selectAll("g.node").data(nodes, function(d) {
				return d.id || (d.id = ++i);
			});

			// Enter the nodes.
			var nodeEnter = node.enter().append("g").attr("class", "node").attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			});

			nodeEnter.append("circle").attr("r", 20).style("stroke", function(d) {
				return d.strokecolor;
			});

			nodeEnter.append("image").attr("xlink:href", function(d) {
				return d.icon;
			}).attr("x", "-12px").attr("y", "-12px").attr("width", "24px").attr("height", "24px");

			nodeEnter.append("text").attr("y", "-4em").attr("text-anchor", "middle").text(function(d) {
				return d.name;
			}).style("fill-opacity", 1);

			nodeEnter.append("text").attr("y", "-2.5em").attr("text-anchor", "middle").text(function(d) {
				return d.type;
			}).style("fill-opacity", 1);

			nodeEnter.append("text").attr("x", "-2.5em").attr("y", "-0.5em").attr("text-anchor", "middle").text(function(d) {
				return d.inport;
			}).style("fill-opacity", 1);

			nodeEnter.append("text").attr("x", "2.5em").attr("y", "-0.5em").attr("text-anchor", "middle").text(function(d) {
				return d.outport;
			}).style("fill-opacity", 1);

			// Declare the links…
			var link = svg.selectAll("path.link").data(links, function(d) {
				return d.target.id;
			});

			// Enter the links.
			link.enter().insert("path", "g").attr("class", "link").style("stroke", "blue").attr("d", diagonal);

		}
	}
</script>

<style>
.node circle {
	fill: #fff;
	stroke: steelblue;
	stroke-width: 7px;
}

.node text {
	font: 12px sans-serif;
}

.link {
	fill: none;
	stroke: #ccc;
	stroke-width: 2px;
}
</style>

</head>

<body>
	<jsp:include page="/Topology">
		<jsp:param value="tree_view" name="action" />
		<jsp:param value="${param.select_ip}" name="select_ip" />
		<jsp:param value="${param.display_type}" name="display_type" />
	</jsp:include>

	<jsp:include page="/Topology">
		<jsp:param value="top_device_get" name="action" />
	</jsp:include>

	<jsp:include page="/Topology">
		<jsp:param value="path_status" name="action" />
		<jsp:param value="${param.src}" name="src" />
		<jsp:param value="${param.dst}" name="dst " />
	</jsp:include>

	<div id="message"></div>

	<c:out value="<script type='text/javascript'>$(document).ready(update(${json_data}));</script>" escapeXml="false"></c:out>
</body>
</html>