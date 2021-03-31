/**
 * Renders text box.
 * @param props
 */
function TextBox(props) {
  const mutate = (event) => {
    props.change(event.target.value);
  }
  return (
    <>
      <label>
        {props.label}
        <input className="text-box" type={props.type} placeholder={props.initial} onChange={mutate}/>
      </label>
    </>
  )
}

export default TextBox;