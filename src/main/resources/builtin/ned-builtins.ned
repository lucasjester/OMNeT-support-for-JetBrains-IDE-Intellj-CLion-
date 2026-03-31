package ned;
@namespace("omnetpp");

channel IdealChannel
{
    @class(cIdealChannel);
}

channel DelayChannel
{
    @class(cDelayChannel);

    bool disabled @mutable = default(false);
    double delay @mutable = default(0s) @unit(s);
}

channel DatarateChannel
{
    @class(cDatarateChannel);

    bool disabled @mutable = default(false);
    double delay @mutable = default(0s) @unit(s);
    double datarate @mutable = default(0bps) @unit(bps);
    double ber @mutable = default(0);
    double per @mutable = default(0);
}

