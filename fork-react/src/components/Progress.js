import {useEffect, useState} from "react";

/**
 * Renders stars for reviews.
 */
function Progress(props) {
  const [dots, setDots] = useState("");

  // converts reviews to stars
  useEffect(() => {
    const n = props.number;
    let ret = "";
    for (let i = 0; i < props.total; i++) {
      let type = "\u25cb"
      if (i < n) {
        type = "\u25cf"
      }
      if (i === 0) {
        ret += type;
      } else {
        ret += " " + type;
      }
    }
    setDots(ret);
  }, [props.number]);

  return (
    <span className="progress">
      {dots}
    </span>
  );
}

export default Progress;