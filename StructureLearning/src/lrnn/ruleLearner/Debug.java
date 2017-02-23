/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.ruleLearner;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Constant;
import ida.ilp.logic.Literal;
import ida.ilp.logic.LogicUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

/**
 * Created by gusta on 21.2.17.
 */
public class Debug {

    public static void main1(String[] args) {
        Clause clause = Clause.parse("bc1(b17080), bc1(b17069), bc0(b17082), bc1(b17094), atc1(tr159_3), atc0(tr159_2), atc2(tr159_6), atc2(tr159_8), bc0(b17067), atc1(tr159_5), bc1(b17068), bc1(b17081), bc0(b17081), bc1(b17093), atc0(tr159_12), bc2(b17090), bond(tr159_2, tr159_3, b17075), bc0(b17080), bc0(b17068), bc0(b17084), atc2(tr159_13), bc1(b17067), bc2(b17069), bc1(b17092), bc1(b17082), bond(tr159_6, tr159_5, b17080), atc0(tr159_4), bc2(b17080), bc0(b17085), bc0(b17097), bond(tr159_10, tr159_4, b17092), atc1(tr159_1), atc0(tr159_9), bc1(b17066), bc1(b17091), bc2(b17068), bc1(b17083), bond(tr159_4, tr159_5, b17089), bond(tr159_2, tr159_13, b17073), bc0(b17086), bc0(b17096), bc2(b17081), atc0(tr159_10), atc2(tr159_15), bc0(b17066), bc1(b17079), bc2(b17079), atc1(tr159_12), bc1(b17084), bond(tr159_5, tr159_4, b17088), bond(tr159_15, tr159_11, b17076), bc0(b17079), bc0(b17087), atc2(tr159_2), bc2(b17082), bc2(b17094), bc1(b17078), atc1(tr159_13), atc0(tr159_7), atc1(tr159_8), bond(tr159_9, tr159_8, b17096), bond(tr159_3, tr159_2, b17074), bc1(b17085), bc2(b17093), bond(tr159_5, tr159_6, b17081), bc1(b17097), bond(tr159_8, tr159_7, b17090), atc2(tr159_1), bc1(b17077), bc2(b17083), bc0(b17088), bc0(b17078), atc0(tr159_8), bc2(b17085), bc1(b17096), bc0(b17069), bond(tr159_12, tr159_1, b17084), bc1(b17086), bc2(b17084), bc2(b17092), bond(tr159_10, tr159_8, b17094), bc2(b17077), bond(tr159_1, tr159_11, b17083), bc0(b17089), bond(tr159_11, tr159_15, b17077), bond(tr159_4, tr159_3, b17070), atc1(tr159_7), bc1(b17095), bond(tr159_11, tr159_1, b17082), bc2(b17091), atc1(tr159_2), bc1(b17087), bc2(b17078), atc2(tr159_7), atc1(tr159_4), bc2(b17075), bc2(b17087), bc1(b17074), atc0(tr159_11), bc1(b17088), bc0(b17075), atc2(tr159_14), bc2(b17076), bc2(b17086), bc1(b17073), bc0(b17074), bc0(b17090), atc2(tr159_9), atc0(tr159_3), bc2(b17073), atc2(tr159_5), bc1(b17089), bond(tr159_3, tr159_4, b17071), atc1(tr159_6), atc0(tr159_13), bond(tr159_11, tr159_10, b17086), bond(tr159_8, tr159_10, b17095), bc1(b17076), atc1(tr159_14), bc0(b17077), atc2(tr159_12), atc2(tr159_10), bc2(b17089), bond(tr159_8, tr159_9, b17097), bc2(b17074), atc1(tr159_11), bond(tr159_1, tr159_12, b17085), bc2(b17095), bc1(b17075), bc0(b17076), bc2(b17088), atc2(tr159_3), atc0(tr159_1), bc1(b17090), atc0(tr159_15), bc1(b17070), bc0(b17070), bc0(b17094), atc0(tr159_6), atc1(tr159_9), bond(tr159_7, tr159_8, b17091), bc2(b17070), bc0(b17095), bc2(b17067), bond(tr159_5, tr159_7, b17079), bc0(b17071), bond(tr159_1, tr159_2, b17067), bc2(b17096), bond(tr159_7, tr159_5, b17078), bc2(b17066), bond(tr159_10, tr159_11, b17087), bc0(b17072), bond(tr159_4, tr159_10, b17093), bc0(b17092), bond(tr159_14, tr159_3, b17068), bond(tr159_2, tr159_1, b17066), bc1(b17072), bc2(b17097), atc1(tr159_10), bc0(b17091), bc2(b17072), bc0(b17073), atc2(tr159_4), bc0(b17083), bc0(b17093), atc0(tr159_14), bond(tr159_3, tr159_14, b17069), bc1(b17071), atc0(tr159_5), bond(tr159_13, tr159_2, b17072), atc1(tr159_15), bc2(b17071), atc2(tr159_11)");

        for (Constant c : LogicUtils.constants(clause)) {
            System.out.println("For " + c);
            Collection<Literal> lits = clause.getLiteralsByTerm(c);
            System.out.println(lits);
            System.out.println("--------");
        }
    }

    public static void main(String[] args) throws IOException {
        String path = "../in/nci/";
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                File mkdirs = new File(path + "/" + file.getName().substring(12, file.getName().indexOf(".")));
                mkdirs.mkdirs();
                Files.copy(file.toPath(), new File(mkdirs.toPath() + "/" + file.getName().substring(12, file.getName().length())).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}