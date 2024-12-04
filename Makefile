PY4J_PATH=${VIRTUAL_ENV}/share/py4j/py4j0.10.9.7.jar
CLASSPATH=./build:/usr/share/beast2-mcmc/beast.jar:/usr/share/java/antlr4-runtime.jar:${PY4J_PATH}

run: compile
	java -cp ${CLASSPATH} DistanceCalculator

compile:
	@echo "Compiling..."
	@javac -cp ${CLASSPATH} -d build tauSpace/*.java
	@echo "Compilation complete."

clean:
	@echo "Cleaning..."
	@rm -rf build/*
	@echo "Clean complete."

test: compile
	java -cp ./build:/usr/share/beast2-mcmc/beast.jar:/usr/share/java/antlr4-runtime.jar Main < test.nex
