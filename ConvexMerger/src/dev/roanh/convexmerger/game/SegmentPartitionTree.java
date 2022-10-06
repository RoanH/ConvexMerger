package dev.roanh.convexmerger.game;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SegmentPartitionTree{
	private final KDTree kdTree;
	
	private SegmentPartitionTree(List<Point2D> points){
		kdTree = new KDTree(points);
	}
	
	public void addSegment(Point2D from, Point2D to){
		addSegment(new Line2D.Double(from, to));
	}
	
	public void addSegment(Line2D line){
		addSegment(kdTree, line);
	}
	
	private void addSegment(KDTree node, Line2D line){
		if(node.contains(line)){
			KDTree low = node.getLowNode();
			if(low == null){
				node.addData(line);
			}else{
				addSegment(low, line);
			}
			
			KDTree high = node.getHighNode();
			if(high == null){
				node.addData(line);
			}else{
				addSegment(high, line);
			}
		}
	}
	
	
	
	
	public void render(Graphics2D g){
		kdTree.render(g);
	}
	
	
	
	//no overlap
	public static final SegmentPartitionTree fromObjects(List<ConvexObject> objects){
		SegmentPartitionTree tree = new SegmentPartitionTree(objects.stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList()));
		
		for(ConvexObject obj : objects){
			List<Point2D> points = obj.getPoints();
			for(int i = 0; i < points.size(); i++){
				tree.addSegment(points.get(i), points.get((i + 1) % points.size()));
			}
		}
		
		return tree;
	}
	
	//no overlap
	public static final SegmentPartitionTree fromLines(List<Line2D> lines){
		SegmentPartitionTree tree = new SegmentPartitionTree(lines.stream().flatMap(line->Stream.of(line.getP1(), line.getP2())).collect(Collectors.toList()));
		lines.forEach(line->tree.addSegment(line.getP1(), line.getP2()));
		return tree;
	}
	
	//TODO probably remove
	public static final SegmentPartitionTree fromPoints(List<Point2D> points){
		return new SegmentPartitionTree(points);
	}
	
	
	
	
	
	
	
	public class LineSegment{
		
		
		
	}
}
