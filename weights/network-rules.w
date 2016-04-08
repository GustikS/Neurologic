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
0,005658328760472 atomKappa_3(X) :- atomLambda_8(X).
0,029675755829441 atomKappa_3(X) :- atomLambda_7(X).
0,210005649715328 atomKappa_3(X) :- atomLambda_6(X).
0,204612196628885 atomKappa_3(X) :- atomLambda_5(X).
0,504120128788967 atomKappa_3(X) :- atomLambda_4(X).
1,005909158305414 atomKappa_3(X) :- atomLambda_3(X).
0,149869400814239 atomKappa_3(X) :- atomLambda_2(X).
0,373884346004333 atomKappa_3(X) :- atomLambda_1(X).
0,063568783733729 atomKappa_2(X) :- atomLambda_8(X).
0,004245867287494 atomKappa_2(X) :- atomLambda_7(X).
0,476165227819303 atomKappa_2(X) :- atomLambda_6(X).
0,386520957431765 atomKappa_2(X) :- atomLambda_5(X).
0,020782230810882 atomKappa_2(X) :- atomLambda_4(X).
0,124923968676519 atomKappa_2(X) :- atomLambda_3(X).
0,034386873084106 atomKappa_2(X) :- atomLambda_2(X).
0,039481196735007 atomKappa_2(X) :- atomLambda_1(X).
0,063046781468975 bondKappa_3(X) :- bondLambda_6(X).
0,007579320426087 bondKappa_3(X) :- bondLambda_5(X).
0,341151506328023 bondKappa_3(X) :- bondLambda_4(X).
0,024345189030669 bondKappa_3(X) :- bondLambda_3(X).
0,001284122616553 bondKappa_3(X) :- bondLambda_2(X).
0,047093560317855 bondKappa_3(X) :- bondLambda_1(X).
0,180871316834910 bondKappa_2(X) :- bondLambda_6(X).
0,799908777343090 bondKappa_2(X) :- bondLambda_5(X).
0,011017020885751 bondKappa_2(X) :- bondLambda_4(X).
0,047827313895731 bondKappa_2(X) :- bondLambda_3(X).
0,300259169985990 bondKappa_2(X) :- bondLambda_2(X).
0,066194130402259 bondKappa_2(X) :- bondLambda_1(X).
0,071847709706917 bondKappa_1(X) :- bondLambda_6(X).
0,869351219220139 bondKappa_1(X) :- bondLambda_5(X).
0,030786589182883 bondKappa_1(X) :- bondLambda_4(X).
0,294637492739696 bondKappa_1(X) :- bondLambda_3(X).
0,331714024508266 bondKappa_1(X) :- bondLambda_2(X).
0,095632710114426 bondKappa_1(X) :- bondLambda_1(X).
0,057228973871386 atomKappa_1(X) :- atomLambda_8(X).
0,139993813619824 atomKappa_1(X) :- atomLambda_7(X).
0,001827385102345 atomKappa_1(X) :- atomLambda_6(X).
0,049524099540299 atomKappa_1(X) :- atomLambda_5(X).
0,172930052299693 atomKappa_1(X) :- atomLambda_4(X).
0,091688733426421 atomKappa_1(X) :- atomLambda_3(X).
0,433210304197605 atomKappa_1(X) :- atomLambda_2(X).
0,007087500839328 atomKappa_1(X) :- atomLambda_1(X).
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
0,174105882535329 finalKappa(DMY) :- lambda_242(DMY2).
0,005500349567734 finalKappa(DMY) :- lambda_241(DMY2).
0,583627172114920 finalKappa(DMY) :- lambda_240(DMY2).
0,016058158444334 finalKappa(DMY) :- lambda_239(DMY2).
0,301558949754130 finalKappa(DMY) :- lambda_238(DMY2).
0,007428644662856 finalKappa(DMY) :- lambda_237(DMY2).
0,046798501264179 finalKappa(DMY) :- lambda_236(DMY2).
0,409512244714062 finalKappa(DMY) :- lambda_235(DMY2).
0,274937137822691 finalKappa(DMY) :- lambda_234(DMY2).
0,254400714176864 finalKappa(DMY) :- lambda_233(DMY2).
1,204763969805519 finalKappa(DMY) :- lambda_232(DMY2).
0,266511945065483 finalKappa(DMY) :- lambda_231(DMY2).
0,251937580208800 finalKappa(DMY) :- lambda_230(DMY2).
0,357282846552957 finalKappa(DMY) :- lambda_229(DMY2).
0,196290571179505 finalKappa(DMY) :- lambda_228(DMY2).
0,050828176243259 finalKappa(DMY) :- lambda_227(DMY2).
0,036574304381643 finalKappa(DMY) :- lambda_226(DMY2).
0,003328570519292 finalKappa(DMY) :- lambda_225(DMY2).
0,238053107973487 finalKappa(DMY) :- lambda_224(DMY2).
0,025848548070753 finalKappa(DMY) :- lambda_223(DMY2).
0,058555274070327 finalKappa(DMY) :- lambda_222(DMY2).
0,164765155639008 finalKappa(DMY) :- lambda_221(DMY2).
0,034123667773050 finalKappa(DMY) :- lambda_220(DMY2).
0,259690502523222 finalKappa(DMY) :- lambda_219(DMY2).
0,571564318548633 finalKappa(DMY) :- lambda_218(DMY2).
0,038809753625607 finalKappa(DMY) :- lambda_217(DMY2).
0,030785204895054 finalKappa(DMY) :- lambda_216(DMY2).
0,170813995442169 finalKappa(DMY) :- lambda_215(DMY2).
0,138614088764555 finalKappa(DMY) :- lambda_214(DMY2).
0,093220267890981 finalKappa(DMY) :- lambda_213(DMY2).
0,259911480900309 finalKappa(DMY) :- lambda_212(DMY2).
0,033041068328874 finalKappa(DMY) :- lambda_211(DMY2).
0,034694705564235 finalKappa(DMY) :- lambda_210(DMY2).
0,210676137991870 finalKappa(DMY) :- lambda_209(DMY2).
0,366802957405906 finalKappa(DMY) :- lambda_208(DMY2).
0,399177355376599 finalKappa(DMY) :- lambda_207(DMY2).
0,272045074237385 finalKappa(DMY) :- lambda_206(DMY2).
0,239268976854680 finalKappa(DMY) :- lambda_205(DMY2).
0,168891219433146 finalKappa(DMY) :- lambda_204(DMY2).
0,063744954157716 finalKappa(DMY) :- lambda_203(DMY2).
0,252540972573753 finalKappa(DMY) :- lambda_202(DMY2).
0,046897434573427 finalKappa(DMY) :- lambda_201(DMY2).
0,638272498009901 finalKappa(DMY) :- lambda_200(DMY2).
1,025494240586376 finalKappa(DMY) :- lambda_199(DMY2).
0,125087168041865 finalKappa(DMY) :- lambda_198(DMY2).
0,188802979912831 finalKappa(DMY) :- lambda_197(DMY2).
0,180778722757035 finalKappa(DMY) :- lambda_196(DMY2).
0,031356311205366 finalKappa(DMY) :- lambda_195(DMY2).
0,026079673616152 finalKappa(DMY) :- lambda_194(DMY2).
0,013037351497326 finalKappa(DMY) :- lambda_193(DMY2).
0,425154627213688 finalKappa(DMY) :- lambda_192(DMY2).
0,077990714445477 finalKappa(DMY) :- lambda_191(DMY2).
0,034721524238567 finalKappa(DMY) :- lambda_190(DMY2).
0,226986772154207 finalKappa(DMY) :- lambda_189(DMY2).
0,201048438314166 finalKappa(DMY) :- lambda_188(DMY2).
0,120658744027169 finalKappa(DMY) :- lambda_187(DMY2).
0,280246442012738 finalKappa(DMY) :- lambda_186(DMY2).
0,192161727567207 finalKappa(DMY) :- lambda_185(DMY2).
0,343198581525678 finalKappa(DMY) :- lambda_184(DMY2).
0,107667984910229 finalKappa(DMY) :- lambda_183(DMY2).
0,237496820273112 finalKappa(DMY) :- lambda_182(DMY2).
0,324966850498704 finalKappa(DMY) :- lambda_181(DMY2).
0,174997223373893 finalKappa(DMY) :- lambda_180(DMY2).
0,149051034980635 finalKappa(DMY) :- lambda_179(DMY2).
0,260648729338460 finalKappa(DMY) :- lambda_178(DMY2).
0,650157127892186 finalKappa(DMY) :- lambda_177(DMY2).
0,071044198722859 finalKappa(DMY) :- lambda_176(DMY2).
0,204092416922217 finalKappa(DMY) :- lambda_175(DMY2).
0,344639117570251 finalKappa(DMY) :- lambda_174(DMY2).
0,020348629785694 finalKappa(DMY) :- lambda_173(DMY2).
0,132930423571636 finalKappa(DMY) :- lambda_172(DMY2).
0,404692975265164 finalKappa(DMY) :- lambda_171(DMY2).
0,071643712390264 finalKappa(DMY) :- lambda_170(DMY2).
0,008755894352614 finalKappa(DMY) :- lambda_169(DMY2).
0,127068954315359 finalKappa(DMY) :- lambda_168(DMY2).
0,204136082003817 finalKappa(DMY) :- lambda_167(DMY2).
0,525436067928037 finalKappa(DMY) :- lambda_166(DMY2).
0,208454177952422 finalKappa(DMY) :- lambda_165(DMY2).
0,063768609631881 finalKappa(DMY) :- lambda_164(DMY2).
0,043460146090954 finalKappa(DMY) :- lambda_163(DMY2).
0,072108408492847 finalKappa(DMY) :- lambda_162(DMY2).
0,133695481846845 finalKappa(DMY) :- lambda_161(DMY2).
0,510076576396070 finalKappa(DMY) :- lambda_160(DMY2).
0,065904654898320 finalKappa(DMY) :- lambda_159(DMY2).
0,072632270097294 finalKappa(DMY) :- lambda_158(DMY2).
1,181548134892166 finalKappa(DMY) :- lambda_157(DMY2).
0,190798578099050 finalKappa(DMY) :- lambda_156(DMY2).
0,100509056027471 finalKappa(DMY) :- lambda_155(DMY2).
0,104773218170504 finalKappa(DMY) :- lambda_154(DMY2).
0,120259737515665 finalKappa(DMY) :- lambda_153(DMY2).
0,036564273632660 finalKappa(DMY) :- lambda_152(DMY2).
0,142906559922306 finalKappa(DMY) :- lambda_151(DMY2).
0,164622063937198 finalKappa(DMY) :- lambda_150(DMY2).
0,078240272297684 finalKappa(DMY) :- lambda_149(DMY2).
0,038909344249687 finalKappa(DMY) :- lambda_148(DMY2).
0,446572739385438 finalKappa(DMY) :- lambda_147(DMY2).
0,380626696731436 finalKappa(DMY) :- lambda_146(DMY2).
0,214023034121013 finalKappa(DMY) :- lambda_145(DMY2).
0,006986378770305 finalKappa(DMY) :- lambda_144(DMY2).
0,107581088223869 finalKappa(DMY) :- lambda_143(DMY2).
0,315303922810118 finalKappa(DMY) :- lambda_142(DMY2).
0,634074907535076 finalKappa(DMY) :- lambda_141(DMY2).
0,017038805808145 finalKappa(DMY) :- lambda_140(DMY2).
0,073145251577598 finalKappa(DMY) :- lambda_139(DMY2).
0,016256794796636 finalKappa(DMY) :- lambda_138(DMY2).
0,365742241746849 finalKappa(DMY) :- lambda_137(DMY2).
0,304879992556900 finalKappa(DMY) :- lambda_136(DMY2).
0,057515050150467 finalKappa(DMY) :- lambda_135(DMY2).
0,253893332238178 finalKappa(DMY) :- lambda_134(DMY2).
0,206751351527103 finalKappa(DMY) :- lambda_133(DMY2).
0,155321725237458 finalKappa(DMY) :- lambda_132(DMY2).
0,077566742007731 finalKappa(DMY) :- lambda_131(DMY2).
0,098205207955335 finalKappa(DMY) :- lambda_130(DMY2).
0,038991818150398 finalKappa(DMY) :- lambda_129(DMY2).
0,162273602977193 finalKappa(DMY) :- lambda_128(DMY2).
0,066758199892980 finalKappa(DMY) :- lambda_127(DMY2).
0,455703411816671 finalKappa(DMY) :- lambda_126(DMY2).
0,143949317799189 finalKappa(DMY) :- lambda_125(DMY2).
0,632082754211753 finalKappa(DMY) :- lambda_124(DMY2).
0,091621407326587 finalKappa(DMY) :- lambda_123(DMY2).
0,230857230605505 finalKappa(DMY) :- lambda_122(DMY2).
0,062120116672229 finalKappa(DMY) :- lambda_121(DMY2).
0,098484273364670 finalKappa(DMY) :- lambda_120(DMY2).
0,378085655584414 finalKappa(DMY) :- lambda_119(DMY2).
0,287180233878548 finalKappa(DMY) :- lambda_118(DMY2).
0,524748392942716 finalKappa(DMY) :- lambda_117(DMY2).
0,241204683658273 finalKappa(DMY) :- lambda_116(DMY2).
0,078568149660555 finalKappa(DMY) :- lambda_115(DMY2).
0,250507582323476 finalKappa(DMY) :- lambda_114(DMY2).
0,744247806557267 finalKappa(DMY) :- lambda_113(DMY2).
0,018513545043046 finalKappa(DMY) :- lambda_112(DMY2).
0,294480083596957 finalKappa(DMY) :- lambda_111(DMY2).
0,011139917258092 finalKappa(DMY) :- lambda_110(DMY2).
0,129955112259058 finalKappa(DMY) :- lambda_109(DMY2).
0,251276483091193 finalKappa(DMY) :- lambda_108(DMY2).
0,098292458979211 finalKappa(DMY) :- lambda_107(DMY2).
0,107619498031738 finalKappa(DMY) :- lambda_106(DMY2).
0,002782836222593 finalKappa(DMY) :- lambda_105(DMY2).
0,203505883203077 finalKappa(DMY) :- lambda_104(DMY2).
0,024262507614987 finalKappa(DMY) :- lambda_103(DMY2).
0,334078600722732 finalKappa(DMY) :- lambda_102(DMY2).
0,499255233197477 finalKappa(DMY) :- lambda_101(DMY2).
0,123220724412827 finalKappa(DMY) :- lambda_100(DMY2).
0,470451561037422 finalKappa(DMY) :- lambda_99(DMY2).
0,122026611965772 finalKappa(DMY) :- lambda_98(DMY2).
0,022335489090032 finalKappa(DMY) :- lambda_97(DMY2).
0,469523835209039 finalKappa(DMY) :- lambda_96(DMY2).
0,583538564568176 finalKappa(DMY) :- lambda_95(DMY2).
0,054059403726466 finalKappa(DMY) :- lambda_94(DMY2).
0,234742637821318 finalKappa(DMY) :- lambda_93(DMY2).
0,321723698702264 finalKappa(DMY) :- lambda_92(DMY2).
0,257362615149097 finalKappa(DMY) :- lambda_91(DMY2).
0,458805463572178 finalKappa(DMY) :- lambda_90(DMY2).
0,060044470843692 finalKappa(DMY) :- lambda_89(DMY2).
0,267752475845889 finalKappa(DMY) :- lambda_88(DMY2).
0,212882539440971 finalKappa(DMY) :- lambda_87(DMY2).
0,297612804208763 finalKappa(DMY) :- lambda_86(DMY2).
0,122205376132548 finalKappa(DMY) :- lambda_85(DMY2).
0,011998180576104 finalKappa(DMY) :- lambda_84(DMY2).
0,102126727115119 finalKappa(DMY) :- lambda_83(DMY2).
0,188314032831046 finalKappa(DMY) :- lambda_82(DMY2).
0,074210162428352 finalKappa(DMY) :- lambda_81(DMY2).
0,417052228858010 finalKappa(DMY) :- lambda_80(DMY2).
0,331724658628040 finalKappa(DMY) :- lambda_79(DMY2).
0,123885092965386 finalKappa(DMY) :- lambda_78(DMY2).
0,226342785799194 finalKappa(DMY) :- lambda_77(DMY2).
0,013579761289655 finalKappa(DMY) :- lambda_76(DMY2).
0,066029787327700 finalKappa(DMY) :- lambda_75(DMY2).
0,403333386649587 finalKappa(DMY) :- lambda_74(DMY2).
0,320169415875476 finalKappa(DMY) :- lambda_73(DMY2).
0,056158060313733 finalKappa(DMY) :- lambda_72(DMY2).
0,224025921801498 finalKappa(DMY) :- lambda_71(DMY2).
0,089745462347032 finalKappa(DMY) :- lambda_70(DMY2).
0,058571902607696 finalKappa(DMY) :- lambda_69(DMY2).
0,039352094335605 finalKappa(DMY) :- lambda_68(DMY2).
0,090096849284027 finalKappa(DMY) :- lambda_67(DMY2).
0,072770906205850 finalKappa(DMY) :- lambda_66(DMY2).
0,682581959397862 finalKappa(DMY) :- lambda_65(DMY2).
0,095298057156564 finalKappa(DMY) :- lambda_64(DMY2).
0,005343780417961 finalKappa(DMY) :- lambda_63(DMY2).
0,120011577832077 finalKappa(DMY) :- lambda_62(DMY2).
0,225813159854912 finalKappa(DMY) :- lambda_61(DMY2).
0,130874152827195 finalKappa(DMY) :- lambda_60(DMY2).
0,106254985647301 finalKappa(DMY) :- lambda_59(DMY2).
0,021851149169187 finalKappa(DMY) :- lambda_58(DMY2).
0,058756409902063 finalKappa(DMY) :- lambda_57(DMY2).
0,077707538454520 finalKappa(DMY) :- lambda_56(DMY2).
0,202075158212276 finalKappa(DMY) :- lambda_55(DMY2).
0,174011747048263 finalKappa(DMY) :- lambda_54(DMY2).
0,073185457492196 finalKappa(DMY) :- lambda_53(DMY2).
0,164614565536910 finalKappa(DMY) :- lambda_52(DMY2).
0,234866861263077 finalKappa(DMY) :- lambda_51(DMY2).
0,022205445489863 finalKappa(DMY) :- lambda_50(DMY2).
0,257111163584167 finalKappa(DMY) :- lambda_49(DMY2).
1,029937575296469 finalKappa(DMY) :- lambda_48(DMY2).
0,344406724041884 finalKappa(DMY) :- lambda_47(DMY2).
0,018159647343307 finalKappa(DMY) :- lambda_46(DMY2).
0,183407993616612 finalKappa(DMY) :- lambda_45(DMY2).
0,279894957689859 finalKappa(DMY) :- lambda_44(DMY2).
0,097317823406559 finalKappa(DMY) :- lambda_43(DMY2).
0,057461242697347 finalKappa(DMY) :- lambda_42(DMY2).
0,069403497709436 finalKappa(DMY) :- lambda_41(DMY2).
0,835930844589504 finalKappa(DMY) :- lambda_40(DMY2).
0,219289687105579 finalKappa(DMY) :- lambda_39(DMY2).
0,068591123982765 finalKappa(DMY) :- lambda_38(DMY2).
0,143838715409444 finalKappa(DMY) :- lambda_37(DMY2).
0,194963950144871 finalKappa(DMY) :- lambda_36(DMY2).
0,127146087908443 finalKappa(DMY) :- lambda_35(DMY2).
0,132416073732189 finalKappa(DMY) :- lambda_34(DMY2).
0,136232185949632 finalKappa(DMY) :- lambda_33(DMY2).
0,085093886947911 finalKappa(DMY) :- lambda_32(DMY2).
0,592040048718292 finalKappa(DMY) :- lambda_31(DMY2).
0,018768086001279 finalKappa(DMY) :- lambda_30(DMY2).
0,096490401154735 finalKappa(DMY) :- lambda_29(DMY2).
0,026313346315243 finalKappa(DMY) :- lambda_28(DMY2).
0,248426803513201 finalKappa(DMY) :- lambda_27(DMY2).
0,016588451682527 finalKappa(DMY) :- lambda_26(DMY2).
0,123127438143650 finalKappa(DMY) :- lambda_25(DMY2).
0,228333678400123 finalKappa(DMY) :- lambda_24(DMY2).
1,040900604461205 finalKappa(DMY) :- lambda_23(DMY2).
0,031744118096292 finalKappa(DMY) :- lambda_22(DMY2).
0,468076189608910 finalKappa(DMY) :- lambda_21(DMY2).
0,201640059684223 finalKappa(DMY) :- lambda_20(DMY2).
0,860289750048894 finalKappa(DMY) :- lambda_19(DMY2).
0,562924538608439 finalKappa(DMY) :- lambda_18(DMY2).
0,032211522793974 finalKappa(DMY) :- lambda_17(DMY2).
0,227548040432763 finalKappa(DMY) :- lambda_16(DMY2).
0,263988967258040 finalKappa(DMY) :- lambda_15(DMY2).
0,134451944752842 finalKappa(DMY) :- lambda_14(DMY2).
0,051765988781648 finalKappa(DMY) :- lambda_13(DMY2).
0,002145358198195 finalKappa(DMY) :- lambda_12(DMY2).
0,005577030871528 finalKappa(DMY) :- lambda_11(DMY2).
0,141412563547600 finalKappa(DMY) :- lambda_10(DMY2).
0,669982617667262 finalKappa(DMY) :- lambda_9(DMY2).
0,503794084159054 finalKappa(DMY) :- lambda_8(DMY2).
0,580871143616610 finalKappa(DMY) :- lambda_7(DMY2).
0,202046008373243 finalKappa(DMY) :- lambda_6(DMY2).
0,006118159220623 finalKappa(DMY) :- lambda_5(DMY2).
0,001586080630474 finalKappa(DMY) :- lambda_4(DMY2).
0,134283227082376 finalKappa(DMY) :- lambda_3(DMY2).
0,031166628665746 finalKappa(DMY) :- lambda_2(DMY2).
0,150232616119785 finalKappa(DMY) :- lambda_1(DMY2).
0,159063158910040 finalKappa(DMY) :- lambda_0(DMY2).
finalLambda :- finalKappa(DMY).
