/*
 * ConvexMerger:  An area maximisation game based on the idea of merging convex shapes.
 * Copyright (C) 2021  Roan Hofland (roan@roanh.dev), Emiliyan Greshkov and contributors.
 * GitHub Repository: https://github.com/RoanH/ConvexMerger
 *
 * ConvexMerger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConvexMerger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.roanh.convexmerger;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import dev.roanh.convexmerger.game.ConvexObject;
import dev.roanh.convexmerger.game.GameState;
import dev.roanh.convexmerger.game.PlayfieldGenerator;
import dev.roanh.convexmerger.player.GreedyPlayer;
import dev.roanh.convexmerger.util.ConjugationTree;
import dev.roanh.convexmerger.util.KDTree;
import dev.roanh.convexmerger.util.SegmentPartitionTree;
import dev.roanh.convexmerger.util.VerticalDecomposition;
import dev.roanh.convexmerger.util.VerticalDecomposition.Trapezoid;

/**
 * Performance evaluation tests for the capita selecta report.
 * @author Roan
 */
public class PerformanceTest{

	public static void main(String[] args) throws Exception{
//		testScalability();
//		computeErrors(Paths.get(""));
//		segmentDistribution();
//		decompDepth();
	}
	
	public static void decompDepth() throws InterruptedException{
		Map<Integer, Integer> trapsByDepth = new HashMap<Integer, Integer>();
		Map<Integer, Integer> trapsByDepthPost = new HashMap<Integer, Integer>();

		for(int i = 0; i < 100; i++){
			PlayfieldGenerator gen = new PlayfieldGenerator();
			gen.setRange(10, 20);
			//gen.setRange(0, 100);
			//gen.setRange(50, 100);
			System.out.println(gen.getSeed());
		
			GameState state = new GameState(gen, Arrays.asList(new GreedyPlayer(), new GreedyPlayer()));
			state.init();
			
			VerticalDecomposition decomp = state.getVerticalDecomposition();
			
			decomp.getTrapezoids().stream().mapToInt(Trapezoid::getDepth).forEach(d->trapsByDepth.merge(d, 1, Integer::sum));
			
			while(!state.isFinished()){
				state.executePlayerTurn();
			}
			
			decomp.getTrapezoids().stream().mapToInt(Trapezoid::getDepth).forEach(d->trapsByDepthPost.merge(d, 1, Integer::sum));
		}
		
		System.out.println("depth pre: ");
		for(int i = 0; i <= trapsByDepth.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + (trapsByDepth.getOrDefault(i, 0) / 100.0D));
		}
		
		System.out.println("depth post: ");
		for(int i = 0; i <= trapsByDepthPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + (trapsByDepthPost.getOrDefault(i, 0) / 100.0D));
		}
	}
	
	public static void segmentDistribution() throws InterruptedException{
		Map<Integer, LongSummaryStatistics> avgByLevelKdCount = new HashMap<Integer, LongSummaryStatistics>();
		Map<Integer, DoubleSummaryStatistics> avgByLevelKdAvg = new HashMap<Integer, DoubleSummaryStatistics>();
		Map<Integer, IntSummaryStatistics> avgByLevelKdMax = new HashMap<Integer, IntSummaryStatistics>();
		Map<Integer, LongSummaryStatistics> avgByLevelCjCount = new HashMap<Integer, LongSummaryStatistics>();
		Map<Integer, DoubleSummaryStatistics> avgByLevelCjAvg = new HashMap<Integer, DoubleSummaryStatistics>();
		Map<Integer, IntSummaryStatistics> avgByLevelCjMax = new HashMap<Integer, IntSummaryStatistics>();
		Map<Integer, LongSummaryStatistics> avgByLevelKdCountPost = new HashMap<Integer, LongSummaryStatistics>();
		Map<Integer, DoubleSummaryStatistics> avgByLevelKdAvgPost = new HashMap<Integer, DoubleSummaryStatistics>();
		Map<Integer, IntSummaryStatistics> avgByLevelKdMaxPost = new HashMap<Integer, IntSummaryStatistics>();
		Map<Integer, LongSummaryStatistics> avgByLevelCjCountPost = new HashMap<Integer, LongSummaryStatistics>();
		Map<Integer, DoubleSummaryStatistics> avgByLevelCjAvgPost = new HashMap<Integer, DoubleSummaryStatistics>();
		Map<Integer, IntSummaryStatistics> avgByLevelCjMaxPost = new HashMap<Integer, IntSummaryStatistics>();

		for(int i = 0; i < 100; i++){
			PlayfieldGenerator gen = new PlayfieldGenerator();
			gen.setRange(10, 20);
			//gen.setRange(0, 100);
			//gen.setRange(50, 100);
			System.out.println(gen.getSeed());
			
			GameState state = new GameState(gen, Arrays.asList(new GreedyPlayer(), new GreedyPlayer()));
			state.init();
			
			SegmentPartitionTree<?> kd = state.getSegmentTreeKD();
			SegmentPartitionTree<?> cj = state.getSegmentTreeConj();
			
			Map<Integer, IntSummaryStatistics> data = new HashMap<Integer, IntSummaryStatistics>();
			kd.streamCells().forEach(cell->data.computeIfAbsent(cell.getDepth(), v->new IntSummaryStatistics()).accept(cell.getData().size()));
			data.entrySet().stream().forEach(e->avgByLevelKdCount.computeIfAbsent(e.getKey(), v->new LongSummaryStatistics()).accept(e.getValue().getSum()));
			data.entrySet().stream().forEach(e->avgByLevelKdAvg.computeIfAbsent(e.getKey(), v->new DoubleSummaryStatistics()).accept(e.getValue().getAverage()));
			data.entrySet().stream().forEach(e->avgByLevelKdMax.computeIfAbsent(e.getKey(), v->new IntSummaryStatistics()).accept(e.getValue().getMax()));

			data.clear();
			cj.streamCells().forEach(cell->data.computeIfAbsent(cell.getDepth(), v->new IntSummaryStatistics()).accept(cell.getData().size()));
			data.entrySet().stream().forEach(e->avgByLevelCjCount.computeIfAbsent(e.getKey(), v->new LongSummaryStatistics()).accept(e.getValue().getSum()));
			data.entrySet().stream().forEach(e->avgByLevelCjAvg.computeIfAbsent(e.getKey(), v->new DoubleSummaryStatistics()).accept(e.getValue().getAverage()));
			data.entrySet().stream().forEach(e->avgByLevelCjMax.computeIfAbsent(e.getKey(), v->new IntSummaryStatistics()).accept(e.getValue().getMax()));
			
			while(!state.isFinished()){
				state.executePlayerTurn();
			}
			
			data.clear();
			kd.streamCells().forEach(cell->data.computeIfAbsent(cell.getDepth(), v->new IntSummaryStatistics()).accept(cell.getData().size()));
			data.entrySet().stream().forEach(e->avgByLevelKdCountPost.computeIfAbsent(e.getKey(), v->new LongSummaryStatistics()).accept(e.getValue().getSum()));
			data.entrySet().stream().forEach(e->avgByLevelKdAvgPost.computeIfAbsent(e.getKey(), v->new DoubleSummaryStatistics()).accept(e.getValue().getAverage()));
			data.entrySet().stream().forEach(e->avgByLevelKdMaxPost.computeIfAbsent(e.getKey(), v->new IntSummaryStatistics()).accept(e.getValue().getMax()));

			data.clear();
			cj.streamCells().forEach(cell->data.computeIfAbsent(cell.getDepth(), v->new IntSummaryStatistics()).accept(cell.getData().size()));
			data.entrySet().stream().forEach(e->avgByLevelCjCountPost.computeIfAbsent(e.getKey(), v->new LongSummaryStatistics()).accept(e.getValue().getSum()));
			data.entrySet().stream().forEach(e->avgByLevelCjAvgPost.computeIfAbsent(e.getKey(), v->new DoubleSummaryStatistics()).accept(e.getValue().getAverage()));
			data.entrySet().stream().forEach(e->avgByLevelCjMaxPost.computeIfAbsent(e.getKey(), v->new IntSummaryStatistics()).accept(e.getValue().getMax()));
		}
		
		System.out.println("depth sum kd: ");
		for(int i = 0; i <= avgByLevelKdCount.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdCount.get(i).getAverage());
		}
		
		System.out.println("depth avg kd: ");
		for(int i = 0; i <= avgByLevelKdAvg.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdAvg.get(i).getAverage());
		}
		
		System.out.println("depth max kd: ");
		for(int i = 0; i <= avgByLevelKdMax.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdMax.get(i).getAverage());
		}
		
		System.out.println("depth sum cj: ");
		for(int i = 0; i <= avgByLevelCjCount.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjCount.get(i).getAverage());
		}
		
		System.out.println("depth avg cj: ");
		for(int i = 0; i <= avgByLevelCjAvg.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjAvg.get(i).getAverage());
		}
		
		System.out.println("depth max cj: ");
		for(int i = 0; i <= avgByLevelCjMax.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjMax.get(i).getAverage());
		}
		
		System.out.println("depth sum kd post: ");
		for(int i = 0; i <= avgByLevelKdCountPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdCountPost.get(i).getAverage());
		}
		
		System.out.println("depth avg kd post: ");
		for(int i = 0; i <= avgByLevelKdAvgPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdAvgPost.get(i).getAverage());
		}
		
		System.out.println("depth max kd post: ");
		for(int i = 0; i <= avgByLevelKdMaxPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelKdMaxPost.get(i).getAverage());
		}
		
		System.out.println("depth sum cj post: ");
		for(int i = 0; i <= avgByLevelCjCountPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjCountPost.get(i).getAverage());
		}
		
		System.out.println("depth avg cj post: ");
		for(int i = 0; i <= avgByLevelCjAvgPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjAvgPost.get(i).getAverage());
		}
		
		System.out.println("depth max cj post: ");
		for(int i = 0; i <= avgByLevelCjMaxPost.keySet().stream().mapToInt(Integer::intValue).max().orElse(0); i++){
			System.out.println(i + " | " + avgByLevelCjMaxPost.get(i).getAverage());
		}
	}
	
	public static void computeErrors(Path file) throws IOException{
		List<String> lines = Files.readAllLines(file);
		List<String> edit = new ArrayList<String>();
		
		for(int i = 0; i < lines.size(); i++){
			String s = lines.get(i);
			if(!s.isEmpty()){
				String[] args = s.split(" ");
				s += " += (0,";
				double avg = Double.parseDouble(args[1]);
				double min = Double.parseDouble(args[2]);
				double max = Double.parseDouble(args[3]);
				s += max - avg;
				s += ") -= (0,";
				s += avg - min;
				s += ")";
				edit.add(s);
			}else{
				edit.add("");
			}
		}
		
		Files.write(file, edit, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public static void testScalability() throws InterruptedException{
		IntSummaryStatistics lvds = new IntSummaryStatistics();
		IntSummaryStatistics lkds = new IntSummaryStatistics();
		IntSummaryStatistics lcts = new IntSummaryStatistics();
		IntSummaryStatistics uvds = new IntSummaryStatistics();
		IntSummaryStatistics ukds = new IntSummaryStatistics();
		IntSummaryStatistics ucts = new IntSummaryStatistics();
		DoubleSummaryStatistics avds = new DoubleSummaryStatistics();
		DoubleSummaryStatistics akds = new DoubleSummaryStatistics();
		DoubleSummaryStatistics acts = new DoubleSummaryStatistics();
		IntSummaryStatistics objss = new IntSummaryStatistics();
		IntSummaryStatistics ptss = new IntSummaryStatistics();
		IntSummaryStatistics segss = new IntSummaryStatistics();
		LongSummaryStatistics cvs = new LongSummaryStatistics();
		LongSummaryStatistics cks = new LongSummaryStatistics();
		LongSummaryStatistics ccs = new LongSummaryStatistics();
		
		for(int i = -10; i < 100; i++){
			PlayfieldGenerator gen = new PlayfieldGenerator();
			//gen.setRange(10, 20);//small
			gen.setRange(10, 25);
			//gen.setRange(15, 30);
			//gen.setRange(20, 40);
			//gen.setRange(0, 100);//medium
			//gen.setRange(50, 100);//large
			
			gen.setCoverage(70);
			gen.setScaling(240);
			
			List<ConvexObject> objs = gen.generatePlayfield();
			List<Point2D> points = objs.stream().flatMap(obj->obj.getPoints().stream()).collect(Collectors.toList());
			
			if(i >= 0){
				System.out.print("seed:" + gen.getSeed());
			}
			
			long start = System.nanoTime();
			VerticalDecomposition vd = new VerticalDecomposition(objs);
			long sv = System.nanoTime();
			KDTree<Void> kd = new KDTree<Void>(points);
			long sk = System.nanoTime();
			ConjugationTree<Void> ct = new ConjugationTree<Void>(points);
			long sc = System.nanoTime();
			
			IntSummaryStatistics vdsum = vd.getTrapezoids().stream().mapToInt(Trapezoid::getDepth).summaryStatistics();
			IntSummaryStatistics kdsum = kd.streamLeafCells().mapToInt(KDTree::getDepth).summaryStatistics();
			IntSummaryStatistics ctsum = ct.streamLeafCells().mapToInt(ConjugationTree::getDepth).summaryStatistics();
			
			if(i < 0){
				continue;
			}
			
			lvds.accept(vdsum.getMin());
			lkds.accept(kdsum.getMin());
			lcts.accept(ctsum.getMin());
			uvds.accept(vdsum.getMax());
			ukds.accept(kdsum.getMax());
			ucts.accept(ctsum.getMax());
			avds.accept(vdsum.getAverage());
			akds.accept(kdsum.getAverage());
			acts.accept(ctsum.getAverage());
			objss.accept(objs.size());
			ptss.accept(points.size());
			segss.accept(objs.stream().mapToInt(obj->obj.getPoints().size() + 1).sum());
			cvs.accept((sv - start));
			cks.accept((sk - sv));
			ccs.accept((sc - sk));
			
			System.out.print(" n:" + i);
			System.out.print(" lvd:" + vdsum.getMin());
			System.out.print(" lkd:" + kdsum.getMin());
			System.out.print(" lct:" + ctsum.getMin());
			System.out.print(" uvd:" + vdsum.getMax());
			System.out.print(" ukd:" + kdsum.getMax());
			System.out.print(" uct:" + ctsum.getMax());
			System.out.print(" avd:" + vdsum.getAverage());
			System.out.print(" akd:" + kdsum.getAverage());
			System.out.print(" act:" + ctsum.getAverage());
			System.out.print(" objs:" + objs.size());
			System.out.print(" pts:" + points.size());
			System.out.print(" segs:" + objs.stream().mapToInt(obj->obj.getPoints().size() + 1).sum());
			System.out.print(" cv:" + (sv - start));
			System.out.print(" ck:" + (sk - sv));
			System.out.println(" cc:" + (sc - sk));
		}
		
		System.out.println("lvds: " + lvds.getAverage() + " " + lvds.getMin() + " " + lvds.getMax());
		System.out.println("lkds: " + lkds.getAverage() + " " + lkds.getMin() + " " + lkds.getMax());
		System.out.println("lcts: " + lcts.getAverage() + " " + lcts.getMin() + " " + lcts.getMax());
		System.out.println("uvds: " + uvds.getAverage() + " " + uvds.getMin() + " " + uvds.getMax());
		System.out.println("ukds: " + ukds.getAverage() + " " + ukds.getMin() + " " + ukds.getMax());
		System.out.println("ucts: " + ucts.getAverage() + " " + ucts.getMin() + " " + ucts.getMax());
		System.out.println("avds: " + avds.getAverage() + " " + avds.getMin() + " " + avds.getMax());
		System.out.println("akds: " + akds.getAverage() + " " + akds.getMin() + " " + akds.getMax());
		System.out.println("acts: " + acts.getAverage() + " " + acts.getMin() + " " + acts.getMax());
		System.out.println("objss: " + objss.getAverage() + " " + objss.getMin() + " " + objss.getMax());
		System.out.println("ptss: " + ptss.getAverage() + " " + ptss.getMin() + " " + ptss.getMax());
		System.out.println("segss: " + segss.getAverage() + " " + segss.getMin() + " " + segss.getMax());
		System.out.println("cvs: " + cvs.getAverage() + " " + cvs.getMin() + " " + cvs.getMax());
		System.out.println("cks: " + cks.getAverage() + " " + cks.getMin() + " " + cks.getMax());
		System.out.println("ccs: " + ccs.getAverage() + " " + ccs.getMin() + " " + ccs.getMax());
	}
}
