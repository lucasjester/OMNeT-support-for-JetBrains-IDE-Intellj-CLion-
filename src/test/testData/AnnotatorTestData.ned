simple MySource {
    gates:
        output out;
}

simple MySink {
    gates:
        input in;
}

network TestNetwork {
    submodules:
        src: MySource;
        snk: MySink;
        bad1: <error descr="Unresolved module type 'NonExistentModule'">NonExistentModule</error>;
        bad2: <error descr="Unresolved module type 'MySikn'">MySikn</error>;
     connections:
        src.out --> snk.in;
        <error descr="Unknown submodule 'typo1'">typo1</error>.out --> <error descr="Unknown submodule 'typo2'">typo2</error>.in;
}