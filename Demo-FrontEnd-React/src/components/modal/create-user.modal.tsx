import { Input, Modal } from "antd";
import { useState } from "react";

interface IProps {
  openCreateModal: boolean;
  setOpenCreateModal: (v: boolean) => void;
}
const CreateUserModal = (props: IProps) => {
  const { openCreateModal, setOpenCreateModal } = props;
  const [name, setName] = useState<string>("");
  const [email, setEmail] = useState<string>("");

  const handleSubmit = () => {
    console.log(">>> check: ", name, email);
  }

  return (
    <Modal
      title="Create a new user"
      maskClosable={false}
      closable={{ "aria-label": "Custom Close Button" }}
      open={openCreateModal}
      onOk={handleSubmit}
      onCancel={() => {
        setOpenCreateModal(false);
      }}
      okText={"Create"}
    >
      <div style={{ display: "flex", flexDirection: "column", gap: 10, marginBottom: 15 }}>
        <span>Name: </span>
        <Input 
        value={name}
        onChange={(v) => setName(v.target.value)}/>
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
        <span>Email: </span>
        <Input 
        value={email}
        onChange={(v) => setEmail(v.target.value)}/>
      </div>
    </Modal>
  );
};

export default CreateUserModal;
