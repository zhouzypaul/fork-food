/**
 * Renders text box.
 * @param props
 */
function TextBox(props) {
  // sets value on change
  const mutate = (event) => {
    props.change(event.target.value);
  }

  return (
    <>
      <label>
        {props.label}
        <input className="text-box" type={props.type} placeholder={props.initial} onChange={mutate} onKeyDown={props.onKeyDown}/>
      </label>
    </>
  )
}

export default TextBox;