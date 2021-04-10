import { useRef } from "react";
import TopBar from "./TopBar";
import Host from "./Host";

function WaitingRoom(props) {

    const roomCode = useRef(9999);
    const host = useRef(false);

    const roomProps = props.location.roomProps
    console.log(roomProps)
    if (roomProps) {
        roomCode.current = roomProps.roomCode
        host.current = roomProps.host
    }
    console.log("heyyo" + host.current)

    return (
        <Host code={roomCode.current} host={host.current} />
    )
}

export default WaitingRoom;