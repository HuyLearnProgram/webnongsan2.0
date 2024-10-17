import React,{memo} from 'react'
// import clsx from 'clsx';
const InputFormAdmin = ({
    label,
    disabled,
    register,
    errors,
    id,
    validate,
    type='text',
    className,
    // placeholder,
    defaultValue
}) => {
    return (
        <div className='flex flex-col h-[78px] gap-2 '>
        {label && <label htmlFor={id}>{label}</label>}
        <input
            type={type}
            id={id}
            {...register(id,validate)}
            disabled={disabled}
            // placeholder={placeholder}
            // className={clsx('form-input my-auto',fullWitdh && 'w-full')}
            className={className}
            defaultValue={defaultValue}
        />
        {errors[id] && <small className='text-x5 text-red-500'>{errors[id]?.message}</small>}
        </div>
      );
}

export default memo(InputFormAdmin)