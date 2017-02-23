package umls

numRelationClusters = 5
numRelationSuClusters = 0

numEntityClusters = 5
numEntitySuClusters = 0

relations = [] as Set
entities = [] as Set

i = 0

//read out elements
new File("umls.txt").eachLine { line ->
    matcher = line =~ /R\((.+),(.+),(.+)\)/
    first = matcher[0][1].toLowerCase()
    second = matcher[0][2].toLowerCase()
    third = matcher[0][3].toLowerCase()

    relations.add(first)
    entities.add(second)
    entities.add(third)
}

//link elements to clusters

relations.each { relation ->
    for (int j = 1; j <= numRelationClusters; j++) {
        println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
        println("holdsL" + i + "(S,P,O) :- @eq(S," + relation.toLowerCase() + "),@eq(P,is),@eq(O,relC" + j + ").")
        i++
    }
}

entities.each { entity ->
    for (int j = 1; j <= numEntityClusters; j++) {
        println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
        println("holdsL" + i + "(S,P,O) :- @eq(S," + entity.toLowerCase() + "),@eq(P,is),@eq(O,entC" + j + ").")
        i++
    }
}

println()
//link clusters to super clusters

for (int k = 1; k <= numRelationSuClusters; k++) {
    for (int j = 1; j <= numRelationClusters; j++) {
        println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
        println("holdsL" + i + "(S,P,O) :- @eq(S,relC" + j + "),@eq(P,is),@eq(O,relSuC" + k + ").")
        i++
    }
}

for (int k = 1; k <= numEntitySuClusters; k++) {
    for (int j = 1; j <= numEntityClusters; j++) {
        println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
        println("holdsL" + i + "(S,P,O) :- @eq(S,entC" + j + "),@eq(P,is),@eq(O,entSuC" + k + ").")
        i++
    }
}

println()

//linking of clusters between themselves

for (int l = 1; l <= numRelationClusters; l++) {
    for (int j = 1; j <= numEntityClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entC" + j + "),@eq(P,relC" + l + "),@eq(O,entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entC" + j + "),@eq(P,relC" + l + "),@eq(O,entSuC" + k + ").")
            i++
        }
    }
    for (int j = 1; j <= numEntitySuClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entSuC" + j + "),@eq(P,relC" + l + "),@eq(O,entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entSuC" + j + "),@eq(P,relC" + l + "),@eq(O,entSuC" + k + ").")
            i++
        }
    }
}

for (int l = 1; l <= numRelationSuClusters; l++) {
    for (int j = 1; j <= numEntityClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entC" + j + "),@eq(P,relSuC" + l + "),@eq(O,entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entC" + j + "),@eq(P,relSuC" + l + "),@eq(O,entSuC" + k + ").")
            i++
        }
    }
    for (int j = 1; j <= numEntitySuClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entSuC" + j + "),@eq(P,relSuC" + l + "),@eq(O,entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println(initWeight() + " holdsK(S,P,O) :- holdsL" + i + "(S,P,O).")
            println("holdsL" + i + "(S,P,O) :- @eq(S,entSuC" + j + "),@eq(P,relSuC" + l + "),@eq(O,entSuC" + k + ").")
            i++
        }
    }
}

println()
//inference umls_rules
for (int l = 1; l <= numRelationClusters; l++) {
    for (int j = 1; j <= numEntityClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entC" + j + "), holdsK(B,is,relC" + l + "), holdsK(C,is,entC" + k + "), holdsK(entC" + j + ",relC" + l + ",entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entC" + j + "), holdsK(B,is,relC" + l + "), holdsK(C,is,entSuC" + k + "), holdsK(entC" + j + ",relC" + l + ",entSuC" + k + ").")
            i++
        }
    }
    for (int j = 1; j <= numEntitySuClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entSuC" + j + "), holdsK(B,is,relC" + l + "), holdsK(C,is,entC" + k + "), holdsK(entSuC" + j + ",relC" + l + ",entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entSuC" + j + "), holdsK(B,is,relC" + l + "), holdsK(C,is,entSuC" + k + "), holdsK(entSuC" + j + ",relC" + l + ",entSuC" + k + ").")
            i++
        }
    }
}

for (int l = 1; l <= numRelationSuClusters; l++) {
    for (int j = 1; j <= numEntityClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entC" + j + "), holdsK(B,is,relSuC" + l + "), holdsK(C,is,entC" + k + "), holdsK(entC" + j + ",relSuC" + l + ",entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entC" + j + "), holdsK(B,is,relSuC" + l + "), holdsK(C,is,entSuC" + k + "), holdsK(entC" + j + ",relSuC" + l + ",entSuC" + k + ").")
            i++
        }
    }
    for (int j = 1; j <= numEntitySuClusters; j++) {
        for (int k = 1; k <= numEntityClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entSuC" + j + "), holdsK(B,is,relSuC" + l + "), holdsK(C,is,entC" + k + "), holdsK(entSuC" + j + ",relSuC" + l + ",entC" + k + ").")
            i++
        }
        for (int k = 1; k <= numEntitySuClusters; k++) {
            println("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).")
            println("holdsLrek" + i + "(A,B,C) :- holdsK(A,is,entSuC" + j + "), holdsK(B,is,relSuC" + l + "), holdsK(C,is,entSuC" + k + "), holdsK(entSuC" + j + ",relSuC" + l + ",entSuC" + k + ").")
            i++
        }
    }
}

println()

println("1.0 holdsK(A,B,C) :- holdsLrek2(A,B,C).")
println("holdsLrek2(A,B,C) :- holdsK(A,is,D), @eq(B,is), holdsK(D,is,C).")

def initWeight() {
    return 2 * (Math.random() - 0.5)
    //return 0.5
}
