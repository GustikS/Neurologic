bondLambda_6(X) :- 7(X).
bondLambda_5(X) :- 5(X).
bondLambda_4(X) :- 4(X).
bondLambda_3(X) :- 3(X).
bondLambda_2(X) :- 2(X).
bondLambda_1(X) :- 1(X).
atomLambda_8(X) :- br(X).
atomLambda_7(X) :- h(X).
atomLambda_6(X) :- i(X).
atomLambda_5(X) :- f(X).
atomLambda_4(X) :- cl(X).
atomLambda_3(X) :- c(X).
atomLambda_2(X) :- n(X).
atomLambda_1(X) :- o(X).
-0.036309354961609 atomKappa_3(X) :- atomLambda_8(X).
-2.291935669750184 atomKappa_3(X) :- atomLambda_7(X).
0.163331062953369 atomKappa_3(X) :- atomLambda_6(X).
1.041075140792986 atomKappa_3(X) :- atomLambda_5(X).
1.584519278531952 atomKappa_3(X) :- atomLambda_4(X).
4.601021908780599 atomKappa_3(X) :- atomLambda_3(X).
-1.685830769888370 atomKappa_3(X) :- atomLambda_2(X).
-2.134802756324066 atomKappa_3(X) :- atomLambda_1(X).
1.076423057944518 atomKappa_2(X) :- atomLambda_8(X).
1.807600628328947 atomKappa_2(X) :- atomLambda_7(X).
0.270274484326083 atomKappa_2(X) :- atomLambda_6(X).
1.965932349030594 atomKappa_2(X) :- atomLambda_5(X).
3.303332950555671 atomKappa_2(X) :- atomLambda_4(X).
-10.125612633174322 atomKappa_2(X) :- atomLambda_3(X).
8.405354033084523 atomKappa_2(X) :- atomLambda_2(X).
-3.141122758592062 atomKappa_2(X) :- atomLambda_1(X).
-5.372722169224384 bondKappa_3(X) :- bondLambda_6(X).
0.536515265601853 bondKappa_3(X) :- bondLambda_5(X).
0.510173638290543 bondKappa_3(X) :- bondLambda_4(X).
0.164306744704592 bondKappa_3(X) :- bondLambda_3(X).
-3.302404525817194 bondKappa_3(X) :- bondLambda_2(X).
7.368094990893267 bondKappa_3(X) :- bondLambda_1(X).
-6.573475402108825 bondKappa_2(X) :- bondLambda_6(X).
0.610036522302762 bondKappa_2(X) :- bondLambda_5(X).
0.664703635016878 bondKappa_2(X) :- bondLambda_4(X).
0.185373612455661 bondKappa_2(X) :- bondLambda_3(X).
-4.869858389996278 bondKappa_2(X) :- bondLambda_2(X).
10.384628811309952 bondKappa_2(X) :- bondLambda_1(X).
7.134391078583527 bondKappa_1(X) :- bondLambda_6(X).
0.379357989812845 bondKappa_1(X) :- bondLambda_5(X).
0.054460934065684 bondKappa_1(X) :- bondLambda_4(X).
0.126004791987463 bondKappa_1(X) :- bondLambda_3(X).
5.836441093091736 bondKappa_1(X) :- bondLambda_2(X).
-12.591788995623396 bondKappa_1(X) :- bondLambda_1(X).
0.253290246385873 atomKappa_1(X) :- atomLambda_8(X).
-10.250976565884672 atomKappa_1(X) :- atomLambda_7(X).
0.323588540801645 atomKappa_1(X) :- atomLambda_6(X).
-3.814348046510523 atomKappa_1(X) :- atomLambda_5(X).
-16.557771471241978 atomKappa_1(X) :- atomLambda_4(X).
32.430897826705326 atomKappa_1(X) :- atomLambda_3(X).
-8.404435007059071 atomKappa_1(X) :- atomLambda_2(X).
3.624827691580031 atomKappa_1(X) :- atomLambda_1(X).
lambda_242(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_241(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_240(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_239(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_238(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_237(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_236(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_235(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_234(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_233(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_232(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_231(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_230(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_229(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_228(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_227(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_226(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_225(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_224(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_223(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_222(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_221(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_220(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_219(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_218(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_217(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_216(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_215(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_214(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_213(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_212(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_211(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_210(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_209(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_208(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_207(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_206(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_205(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_204(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_203(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_202(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_201(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_200(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_199(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_198(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_197(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_196(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_195(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_194(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_193(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_192(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_191(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_190(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_189(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_188(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_187(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_186(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_185(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_184(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_183(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_182(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_181(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_180(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_179(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_178(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_177(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_176(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_175(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_174(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_173(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_172(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_171(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_170(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_169(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_168(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_167(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_166(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_165(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_164(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_163(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_162(DMY) :- atomKappa_3(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_161(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_160(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_159(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_158(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_157(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_156(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_155(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_154(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_153(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_152(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_151(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_150(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_149(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_148(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_147(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_146(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_145(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_144(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_143(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_142(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_141(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_140(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_139(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_138(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_137(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_136(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_135(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_134(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_133(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_132(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_131(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_130(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_129(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_128(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_127(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_126(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_125(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_124(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_123(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_122(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_121(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_120(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_119(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_118(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_117(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_116(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_115(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_114(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_113(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_112(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_111(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_110(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_109(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_108(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_107(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_106(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_105(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_104(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_103(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_102(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_101(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_100(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_99(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_98(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_97(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_96(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_95(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_94(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_93(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_92(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_91(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_90(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_89(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_88(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_87(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_86(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_85(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_84(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_83(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_82(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_81(DMY) :- atomKappa_2(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_80(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_79(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_78(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_77(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_76(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_75(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_74(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_73(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_72(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_71(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_70(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_69(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_68(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_67(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_66(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_65(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_64(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_63(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_62(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_61(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_60(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_59(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_58(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_57(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_56(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_55(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_54(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_3(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_53(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_52(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_51(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_50(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_49(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_48(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_47(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_46(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_45(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_44(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_43(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_42(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_41(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_40(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_39(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_38(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_37(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_36(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_35(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_34(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_33(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_32(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_31(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_30(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_29(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_28(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_27(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_2(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_26(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_25(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_24(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_23(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_22(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_21(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_20(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_3(Z).
lambda_19(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_3(Z).
lambda_18(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_3(Z).
lambda_17(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_16(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_15(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_14(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_13(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_12(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_11(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_2(Z).
lambda_10(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_2(Z).
lambda_9(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_2(Z).
lambda_8(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_7(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_6(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_3(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_5(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_4(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_3(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_2(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
lambda_2(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_3(B2),atomKappa_1(Z).
lambda_1(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_2(B2),atomKappa_1(Z).
lambda_0(DMY) :- atomKappa_1(X),bond(X,Y,B1),bondKappa_1(B1),atomKappa_1(Y),bond(Y,Z,B2),bondKappa_1(B2),atomKappa_1(Z).
0.575610624417454 finalKappa(DMY) :- lambda_242(DMY2).
-0.219409210792043 finalKappa(DMY) :- lambda_241(DMY2).
0.503913327935223 finalKappa(DMY) :- lambda_240(DMY2).
-0.194759325914382 finalKappa(DMY) :- lambda_239(DMY2).
-0.616871045739863 finalKappa(DMY) :- lambda_238(DMY2).
0.876122656959963 finalKappa(DMY) :- lambda_237(DMY2).
0.967942641344739 finalKappa(DMY) :- lambda_236(DMY2).
0.812627400368794 finalKappa(DMY) :- lambda_235(DMY2).
-0.221860021920532 finalKappa(DMY) :- lambda_234(DMY2).
0.105976556670973 finalKappa(DMY) :- lambda_233(DMY2).
-0.288293375326488 finalKappa(DMY) :- lambda_232(DMY2).
0.504516393321461 finalKappa(DMY) :- lambda_231(DMY2).
-0.168994632505750 finalKappa(DMY) :- lambda_230(DMY2).
-0.178778706969580 finalKappa(DMY) :- lambda_229(DMY2).
1.211301332833599 finalKappa(DMY) :- lambda_228(DMY2).
0.765380861606590 finalKappa(DMY) :- lambda_227(DMY2).
1.424457212246339 finalKappa(DMY) :- lambda_226(DMY2).
-0.585705458969203 finalKappa(DMY) :- lambda_225(DMY2).
0.783362814977783 finalKappa(DMY) :- lambda_224(DMY2).
1.174804005803421 finalKappa(DMY) :- lambda_223(DMY2).
1.011097073299001 finalKappa(DMY) :- lambda_222(DMY2).
1.263367291838507 finalKappa(DMY) :- lambda_221(DMY2).
2.235386749937693 finalKappa(DMY) :- lambda_220(DMY2).
1.373154085742237 finalKappa(DMY) :- lambda_219(DMY2).
0.947928969183780 finalKappa(DMY) :- lambda_218(DMY2).
1.722838412746257 finalKappa(DMY) :- lambda_217(DMY2).
1.497233682789829 finalKappa(DMY) :- lambda_216(DMY2).
0.134457942438127 finalKappa(DMY) :- lambda_215(DMY2).
-0.387533756662984 finalKappa(DMY) :- lambda_214(DMY2).
0.797746781268522 finalKappa(DMY) :- lambda_213(DMY2).
-0.188844377456200 finalKappa(DMY) :- lambda_212(DMY2).
-1.147669582303393 finalKappa(DMY) :- lambda_211(DMY2).
1.379585521060878 finalKappa(DMY) :- lambda_210(DMY2).
0.749921687826117 finalKappa(DMY) :- lambda_209(DMY2).
1.090584360187280 finalKappa(DMY) :- lambda_208(DMY2).
0.484097802393211 finalKappa(DMY) :- lambda_207(DMY2).
-0.247673684382511 finalKappa(DMY) :- lambda_206(DMY2).
-0.993763581308459 finalKappa(DMY) :- lambda_205(DMY2).
0.539610419846424 finalKappa(DMY) :- lambda_204(DMY2).
-0.402128649891947 finalKappa(DMY) :- lambda_203(DMY2).
-1.479647537497993 finalKappa(DMY) :- lambda_202(DMY2).
1.162228060067800 finalKappa(DMY) :- lambda_201(DMY2).
0.534684006658520 finalKappa(DMY) :- lambda_200(DMY2).
1.287273201458790 finalKappa(DMY) :- lambda_199(DMY2).
0.109080921013025 finalKappa(DMY) :- lambda_198(DMY2).
1.025429899819397 finalKappa(DMY) :- lambda_197(DMY2).
1.441812446098774 finalKappa(DMY) :- lambda_196(DMY2).
1.664544699296883 finalKappa(DMY) :- lambda_195(DMY2).
1.447845933417608 finalKappa(DMY) :- lambda_194(DMY2).
2.854136036405981 finalKappa(DMY) :- lambda_193(DMY2).
2.246337216248714 finalKappa(DMY) :- lambda_192(DMY2).
1.427551164854797 finalKappa(DMY) :- lambda_191(DMY2).
2.001058225004839 finalKappa(DMY) :- lambda_190(DMY2).
1.978737633893133 finalKappa(DMY) :- lambda_189(DMY2).
0.101145815278262 finalKappa(DMY) :- lambda_188(DMY2).
-0.067795070329230 finalKappa(DMY) :- lambda_187(DMY2).
-1.087506316740290 finalKappa(DMY) :- lambda_186(DMY2).
0.078404239771563 finalKappa(DMY) :- lambda_185(DMY2).
-1.504356408977760 finalKappa(DMY) :- lambda_184(DMY2).
-1.671300798232593 finalKappa(DMY) :- lambda_183(DMY2).
-1.108681700375325 finalKappa(DMY) :- lambda_182(DMY2).
-1.608768841654513 finalKappa(DMY) :- lambda_181(DMY2).
-1.380829757408434 finalKappa(DMY) :- lambda_180(DMY2).
-0.491988314304421 finalKappa(DMY) :- lambda_179(DMY2).
-0.830438442461286 finalKappa(DMY) :- lambda_178(DMY2).
-1.544437424259314 finalKappa(DMY) :- lambda_177(DMY2).
-0.793189464444701 finalKappa(DMY) :- lambda_176(DMY2).
-0.648548433670939 finalKappa(DMY) :- lambda_175(DMY2).
-1.456936830520437 finalKappa(DMY) :- lambda_174(DMY2).
-1.542150688558081 finalKappa(DMY) :- lambda_173(DMY2).
-1.415314307418406 finalKappa(DMY) :- lambda_172(DMY2).
-1.781551338500783 finalKappa(DMY) :- lambda_171(DMY2).
1.226684248687170 finalKappa(DMY) :- lambda_170(DMY2).
1.591432644016118 finalKappa(DMY) :- lambda_169(DMY2).
-0.428267980343539 finalKappa(DMY) :- lambda_168(DMY2).
1.507254555807716 finalKappa(DMY) :- lambda_167(DMY2).
0.065808310081424 finalKappa(DMY) :- lambda_166(DMY2).
-0.942216622995868 finalKappa(DMY) :- lambda_165(DMY2).
-0.611881680904742 finalKappa(DMY) :- lambda_164(DMY2).
-1.060510172798073 finalKappa(DMY) :- lambda_163(DMY2).
1.195511844482920 finalKappa(DMY) :- lambda_162(DMY2).
0.596161102017127 finalKappa(DMY) :- lambda_161(DMY2).
-0.202531995711871 finalKappa(DMY) :- lambda_160(DMY2).
0.610001298747366 finalKappa(DMY) :- lambda_159(DMY2).
-0.365575928732462 finalKappa(DMY) :- lambda_158(DMY2).
-0.162846516282815 finalKappa(DMY) :- lambda_157(DMY2).
1.265199994766406 finalKappa(DMY) :- lambda_156(DMY2).
0.766311879790464 finalKappa(DMY) :- lambda_155(DMY2).
1.125318627503100 finalKappa(DMY) :- lambda_154(DMY2).
-1.048849852077571 finalKappa(DMY) :- lambda_153(DMY2).
0.034217607517089 finalKappa(DMY) :- lambda_152(DMY2).
-0.503064456553344 finalKappa(DMY) :- lambda_151(DMY2).
-0.082485220260319 finalKappa(DMY) :- lambda_150(DMY2).
-0.084392206137586 finalKappa(DMY) :- lambda_149(DMY2).
0.474214088397739 finalKappa(DMY) :- lambda_148(DMY2).
0.337176439057179 finalKappa(DMY) :- lambda_147(DMY2).
0.346884546114301 finalKappa(DMY) :- lambda_146(DMY2).
0.318730015107372 finalKappa(DMY) :- lambda_145(DMY2).
-0.502697358427681 finalKappa(DMY) :- lambda_144(DMY2).
1.296673873024751 finalKappa(DMY) :- lambda_143(DMY2).
1.682805593862325 finalKappa(DMY) :- lambda_142(DMY2).
1.231569300258382 finalKappa(DMY) :- lambda_141(DMY2).
1.529425827284302 finalKappa(DMY) :- lambda_140(DMY2).
2.462127126895754 finalKappa(DMY) :- lambda_139(DMY2).
2.261766690611375 finalKappa(DMY) :- lambda_138(DMY2).
1.474377416507350 finalKappa(DMY) :- lambda_137(DMY2).
2.286997724873388 finalKappa(DMY) :- lambda_136(DMY2).
1.774214707577515 finalKappa(DMY) :- lambda_135(DMY2).
0.260610108377105 finalKappa(DMY) :- lambda_134(DMY2).
-0.850837378229512 finalKappa(DMY) :- lambda_133(DMY2).
0.570309510005241 finalKappa(DMY) :- lambda_132(DMY2).
-0.982964644157279 finalKappa(DMY) :- lambda_131(DMY2).
-1.454171211669555 finalKappa(DMY) :- lambda_130(DMY2).
1.017343639302839 finalKappa(DMY) :- lambda_129(DMY2).
0.817785593179195 finalKappa(DMY) :- lambda_128(DMY2).
1.133894838901365 finalKappa(DMY) :- lambda_127(DMY2).
-0.029568763347361 finalKappa(DMY) :- lambda_126(DMY2).
-1.660514980174958 finalKappa(DMY) :- lambda_125(DMY2).
-2.803006996589652 finalKappa(DMY) :- lambda_124(DMY2).
-1.048075062941042 finalKappa(DMY) :- lambda_123(DMY2).
-2.704835844596593 finalKappa(DMY) :- lambda_122(DMY2).
-2.397184022964980 finalKappa(DMY) :- lambda_121(DMY2).
-0.856238218076883 finalKappa(DMY) :- lambda_120(DMY2).
-1.035622147527959 finalKappa(DMY) :- lambda_119(DMY2).
-0.546712754775130 finalKappa(DMY) :- lambda_118(DMY2).
0.611079840411687 finalKappa(DMY) :- lambda_117(DMY2).
1.773346789082285 finalKappa(DMY) :- lambda_116(DMY2).
2.153735131381509 finalKappa(DMY) :- lambda_115(DMY2).
1.971003299917768 finalKappa(DMY) :- lambda_114(DMY2).
2.201313111965762 finalKappa(DMY) :- lambda_113(DMY2).
3.543626364098813 finalKappa(DMY) :- lambda_112(DMY2).
3.375292490020160 finalKappa(DMY) :- lambda_111(DMY2).
2.028730287590195 finalKappa(DMY) :- lambda_110(DMY2).
3.398244486058642 finalKappa(DMY) :- lambda_109(DMY2).
3.037493085767872 finalKappa(DMY) :- lambda_108(DMY2).
0.035252643602588 finalKappa(DMY) :- lambda_107(DMY2).
-0.556624519546015 finalKappa(DMY) :- lambda_106(DMY2).
-1.622134764141527 finalKappa(DMY) :- lambda_105(DMY2).
-0.816953677330162 finalKappa(DMY) :- lambda_104(DMY2).
-0.787449815435748 finalKappa(DMY) :- lambda_103(DMY2).
-1.444077826231976 finalKappa(DMY) :- lambda_102(DMY2).
-1.570035179906255 finalKappa(DMY) :- lambda_101(DMY2).
-1.438905321846802 finalKappa(DMY) :- lambda_100(DMY2).
-2.269453219467946 finalKappa(DMY) :- lambda_99(DMY2).
-0.505982380477133 finalKappa(DMY) :- lambda_98(DMY2).
0.266471995523199 finalKappa(DMY) :- lambda_97(DMY2).
-1.574672563201113 finalKappa(DMY) :- lambda_96(DMY2).
0.851439274307000 finalKappa(DMY) :- lambda_95(DMY2).
1.633087804265850 finalKappa(DMY) :- lambda_94(DMY2).
-0.332569592774056 finalKappa(DMY) :- lambda_93(DMY2).
-1.731466234849185 finalKappa(DMY) :- lambda_92(DMY2).
-0.708717130350779 finalKappa(DMY) :- lambda_91(DMY2).
-1.273980776945653 finalKappa(DMY) :- lambda_90(DMY2).
1.036763194139801 finalKappa(DMY) :- lambda_89(DMY2).
0.461636229526451 finalKappa(DMY) :- lambda_88(DMY2).
-1.166727009592432 finalKappa(DMY) :- lambda_87(DMY2).
0.419683352230140 finalKappa(DMY) :- lambda_86(DMY2).
-1.415116939130083 finalKappa(DMY) :- lambda_85(DMY2).
-1.272024815455650 finalKappa(DMY) :- lambda_84(DMY2).
-1.196871279968934 finalKappa(DMY) :- lambda_83(DMY2).
-1.132873756950221 finalKappa(DMY) :- lambda_82(DMY2).
0.809685946767789 finalKappa(DMY) :- lambda_81(DMY2).
1.181056315969610 finalKappa(DMY) :- lambda_80(DMY2).
1.236176898555704 finalKappa(DMY) :- lambda_79(DMY2).
0.919948673723148 finalKappa(DMY) :- lambda_78(DMY2).
1.164674652500725 finalKappa(DMY) :- lambda_77(DMY2).
1.746722089277044 finalKappa(DMY) :- lambda_76(DMY2).
1.380465626900315 finalKappa(DMY) :- lambda_75(DMY2).
0.893437056554618 finalKappa(DMY) :- lambda_74(DMY2).
1.648629848461144 finalKappa(DMY) :- lambda_73(DMY2).
1.496208711651361 finalKappa(DMY) :- lambda_72(DMY2).
1.233329248975878 finalKappa(DMY) :- lambda_71(DMY2).
1.654160895089529 finalKappa(DMY) :- lambda_70(DMY2).
1.891292633851178 finalKappa(DMY) :- lambda_69(DMY2).
1.722945522785501 finalKappa(DMY) :- lambda_68(DMY2).
2.367372486831259 finalKappa(DMY) :- lambda_67(DMY2).
2.241581115397903 finalKappa(DMY) :- lambda_66(DMY2).
1.257424297923768 finalKappa(DMY) :- lambda_65(DMY2).
2.300298538662223 finalKappa(DMY) :- lambda_64(DMY2).
2.032541292811004 finalKappa(DMY) :- lambda_63(DMY2).
0.978061860728615 finalKappa(DMY) :- lambda_62(DMY2).
1.686387758096480 finalKappa(DMY) :- lambda_61(DMY2).
1.363588722826567 finalKappa(DMY) :- lambda_60(DMY2).
1.709979235683167 finalKappa(DMY) :- lambda_59(DMY2).
3.235002431213237 finalKappa(DMY) :- lambda_58(DMY2).
1.873715818560479 finalKappa(DMY) :- lambda_57(DMY2).
1.418208249974986 finalKappa(DMY) :- lambda_56(DMY2).
1.926244286035929 finalKappa(DMY) :- lambda_55(DMY2).
2.794915267589885 finalKappa(DMY) :- lambda_54(DMY2).
0.988843026253134 finalKappa(DMY) :- lambda_53(DMY2).
1.431697352491996 finalKappa(DMY) :- lambda_52(DMY2).
1.375875149531364 finalKappa(DMY) :- lambda_51(DMY2).
1.998059713375173 finalKappa(DMY) :- lambda_50(DMY2).
2.035052378274231 finalKappa(DMY) :- lambda_49(DMY2).
2.056470149142648 finalKappa(DMY) :- lambda_48(DMY2).
1.476351224936001 finalKappa(DMY) :- lambda_47(DMY2).
2.184972970766566 finalKappa(DMY) :- lambda_46(DMY2).
2.107007090508991 finalKappa(DMY) :- lambda_45(DMY2).
1.554437760470922 finalKappa(DMY) :- lambda_44(DMY2).
2.178878492223870 finalKappa(DMY) :- lambda_43(DMY2).
2.202696341379119 finalKappa(DMY) :- lambda_42(DMY2).
2.253157168687932 finalKappa(DMY) :- lambda_41(DMY2).
3.504226638831701 finalKappa(DMY) :- lambda_40(DMY2).
3.358077981746762 finalKappa(DMY) :- lambda_39(DMY2).
2.062651698477471 finalKappa(DMY) :- lambda_38(DMY2).
3.572690362148312 finalKappa(DMY) :- lambda_37(DMY2).
2.955103290352974 finalKappa(DMY) :- lambda_36(DMY2).
1.439949806919267 finalKappa(DMY) :- lambda_35(DMY2).
2.054755352297431 finalKappa(DMY) :- lambda_34(DMY2).
1.295556998694853 finalKappa(DMY) :- lambda_33(DMY2).
2.230864863390486 finalKappa(DMY) :- lambda_32(DMY2).
2.669675484108832 finalKappa(DMY) :- lambda_31(DMY2).
2.328360176380588 finalKappa(DMY) :- lambda_30(DMY2).
1.335242126197091 finalKappa(DMY) :- lambda_29(DMY2).
1.980696713713359 finalKappa(DMY) :- lambda_28(DMY2).
2.706240794124625 finalKappa(DMY) :- lambda_27(DMY2).
1.196988920849390 finalKappa(DMY) :- lambda_26(DMY2).
1.500523845852714 finalKappa(DMY) :- lambda_25(DMY2).
-0.355769484293159 finalKappa(DMY) :- lambda_24(DMY2).
1.436296597226436 finalKappa(DMY) :- lambda_23(DMY2).
0.186280822298902 finalKappa(DMY) :- lambda_22(DMY2).
-1.072113369280046 finalKappa(DMY) :- lambda_21(DMY2).
-0.649131985901852 finalKappa(DMY) :- lambda_20(DMY2).
-1.110454120337901 finalKappa(DMY) :- lambda_19(DMY2).
1.401057073631846 finalKappa(DMY) :- lambda_18(DMY2).
0.524703749798269 finalKappa(DMY) :- lambda_17(DMY2).
0.992948457143453 finalKappa(DMY) :- lambda_16(DMY2).
-1.133885231804212 finalKappa(DMY) :- lambda_15(DMY2).
0.455179863159358 finalKappa(DMY) :- lambda_14(DMY2).
-1.394581747385791 finalKappa(DMY) :- lambda_13(DMY2).
-1.319310119570921 finalKappa(DMY) :- lambda_12(DMY2).
-1.218431391772545 finalKappa(DMY) :- lambda_11(DMY2).
-1.073860554744946 finalKappa(DMY) :- lambda_10(DMY2).
0.808853436646191 finalKappa(DMY) :- lambda_9(DMY2).
2.529916318098467 finalKappa(DMY) :- lambda_8(DMY2).
3.477319421221538 finalKappa(DMY) :- lambda_7(DMY2).
0.145655385354069 finalKappa(DMY) :- lambda_6(DMY2).
3.217338473605721 finalKappa(DMY) :- lambda_5(DMY2).
2.672075230621194 finalKappa(DMY) :- lambda_4(DMY2).
-0.653802061927616 finalKappa(DMY) :- lambda_3(DMY2).
0.192594581114380 finalKappa(DMY) :- lambda_2(DMY2).
-0.429875674102486 finalKappa(DMY) :- lambda_1(DMY2).
3.165204881985381 finalKappa(DMY) :- lambda_0(DMY2).
finalLambda :- finalKappa(DMY).
