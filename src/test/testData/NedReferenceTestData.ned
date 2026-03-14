simple MySource {
    gates:
        output out;
}

module TestNetwork {
    submodules:
        src: My<caret>Source;
}