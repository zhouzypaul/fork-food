import { useEffect, useRef, useState } from "react";

const MESSAGE_TYPE = {
    CONNECT: 0,
    UPDATE: 1,
    SEND: 2
};

const useSwipeResults = (roomId, user, sock) => {
    const [result, setResult] = useState();

    const socket = useRef();

    socket.current = sock

    useEffect(() => {
        socket.current.onmessage = (msg) => {
            const data = JSON.parse(msg.data);
            console.log(data);
            switch (data.type) {
                case MESSAGE_TYPE.CONNECT:
                    console.log("reached okay")
                    // send a message with our username and room

                    break;
                case MESSAGE_TYPE.UPDATE:
                    // check if we get a finished message then move to next page
                    console.log("herehhhhh")
                    if (data.payload.type === "done") {
                        console.log("herehhhhh")
                        setResult(data.payload.senderMessage.result);
                    }
                    break;
                default:
                    console.log('Unknown message type!', data.type);
                    break;
            }
            socket.current.onclose = () => {
                console.log("socket closed");
            }
        }

    }, [roomId, user])

    return { result };
}

export default useSwipeResults;