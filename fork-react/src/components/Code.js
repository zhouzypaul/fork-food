import {useRef} from "react";

/**
 * Renders code input area.
 */
function Code(props) {
  // references to each text box
  const b1 = useRef(null);
  const b2 = useRef(null);
  const b3 = useRef(null);
  const b4 = useRef(null);

  // jumps forwards and backwards between text boxes
  const jump = (e) => {
    const name = e.target.name;
    if (e.target.value.length > 0) {
      // go forward
      getValue();
      if (name === "b1") {
        b2.current.focus();
      } else if (name === "b2") {
        b3.current.focus();
      } else if (name === "b3") {
        b4.current.focus();
      }
    } else {
      // go backward on backspace
      getValue();
      if (name === "b4") {
        b3.current.select();
      } else if (name === "b3") {
        b2.current.select();
      } else if (name === "b2") {
        b1.current.select();
      }
    }
  }

  // verifies each key press to ensure proper behavior
  const verify = (e) => {
    const key = e.key;
    const name = e.target.name;
    if (e.target.value.length > 0) {
      const selected = document.getSelection().toString();
      if (key === "Enter") {
        props.go();
      } else if (key !== "Backspace" && e.target.value !== selected) {
        // ensure max of one character per text box and type when text is selected
        e.preventDefault();
        e.stopPropagation();
      } else if (key === selected) {
        // allows user to type same character as selected text
        e.preventDefault();
        e.stopPropagation();
        jump(e);
      }
    } else {
      // jumps backward when backspace is pressed when there is no text in box
      if (key === "Backspace") {
        e.preventDefault();
        e.stopPropagation();
        if (name === "b4") {
          b3.current.select();
        } else if (name === "b3") {
          b2.current.select();
        } else if (name === "b2") {
          b1.current.select();
        }
      }
    }
  }

  // selects target
  const select = (e) => {
    e.target.select();
  }

  // gets value and calls setter
  const getValue = () => {
    const ret = b1.current.value + b2.current.value + b3.current.value + b4.current.value;
    props.change(ret);
  }

  return (
    <div className="code-cont">
      <input ref={b1} className="code-input" type="number" name="b1" onChange={jump} onKeyDown={verify} onFocus={select}/>
      <input ref={b2} className="code-input" type="number" name="b2" onChange={jump} onKeyDown={verify} onFocus={select}/>
      <input ref={b3} className="code-input" type="number" name="b3" onChange={jump} onKeyDown={verify} onFocus={select}/>
      <input ref={b4} className="code-input" type="number" name="b4" onChange={jump} onKeyDown={verify} onFocus={select}/>
    </div>
  );
}

export default Code;